package dal.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import dal.ActionResult;
import dal.DataAccessLayer;
import dal.exceptions.CouldNotConnectException;
import dal.exceptions.EntityNotFoundException;
import dal.exceptions.NotConnectedException;
import model.JaratFejlec;
import model.Person;
import model.Jarat;
import model.JaratVonatszam;
import oracle.jdbc.proxy.annotation.Pre;

/**
 * Initial implementation for the DataAccessLayer for the 30-VASUT exercise.
 */
public class VasutDal implements DataAccessLayer<JaratFejlec, Jarat, JaratVonatszam> {
	private Connection connection;
	protected static final String driverName = "oracle.jdbc.driver.OracleDriver";
	protected static final String databaseUrl = "jdbc:oracle:thin:@rapid.eik.bme.hu:1521:szglab";

	private void checkConnected() throws NotConnectedException {
		if (connection == null) {
			throw new NotConnectedException();
		}
	}

	@Override
	public void connect(String username, String password) throws CouldNotConnectException, ClassNotFoundException {
		try {
			if (connection == null || !connection.isValid(30)) {
				if (connection == null) {
					// Load the specified database driver
					Class.forName(driverName);
				} else {
					connection.close();
				}

				// Create new connection and get metadata
				connection = DriverManager.getConnection(databaseUrl, username, password);
			}
		} catch (SQLException e) {
			throw new CouldNotConnectException();

		}
	}

	/**
	 * A megadott keyword alapján kilistázza a JARAT tábla megfelelő rekordjait.
	 * @param keyword The search keyword
	 * @return Visszadja a keresési kulcs alapján talált rekordokat egy listában.
	 * @throws NotConnectedException Ha nem csatlakozunk az adatbázishoz ezt dobja.
	 */
	@Override
	public List<JaratFejlec> search(String keyword) throws NotConnectedException {
		checkConnected();
		List<JaratFejlec> result = new ArrayList<>();
		try (PreparedStatement pstmt = connection.prepareStatement("SELECT * "
				+ "FROM AYC7B1.JARAT WHERE VONATSZAM LIKE ? ")) {
			/* Akármivel lehet keresni, így szimbólummal és számmal is */
			pstmt.setString(1, '%' + keyword + '%');
			try (ResultSet rset = pstmt.executeQuery()) {
				while (rset.next()) {
					JaratFejlec j = new JaratFejlec(rset.getInt("VONATSZAM"),
							rset.getString("TIPUS"),
							rset.getString("MEGJEGYZES"));
					result.add(j);
				}
				return result;
			}
		} catch (SQLException e) {	//ha valami gond adódna
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<JaratVonatszam> getStatistics() throws NotConnectedException {
		checkConnected();
		List<JaratVonatszam> result = new ArrayList<>();
		try (Statement stmt = connection.createStatement()) {
			try (ResultSet rset = stmt.executeQuery("SELECT DISTINCT m1.vonatszam "
					+ " FROM megall m1, (SELECT allomas_id  "
					+ "                 FROM megall m, jarat j "
					+ "                 WHERE m.vonatszam = j.vonatszam "
					+ "                  AND nap = 0100100) s "
					+ " WHERE m1.allomas_id = s.allomas_id "
					+ " ORDER BY m1.vonatszam "
					)){
				while (rset.next()) {
					JaratVonatszam jv = new JaratVonatszam();
					jv.setVonatszam(rset.getInt("VONATSZAM"));
					result.add(jv);
				}
				return result;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean commit() throws NotConnectedException {
		checkConnected();
		return false;
	}

	@Override
	public boolean rollback() throws NotConnectedException {
		checkConnected();
		return false;
	}


	@Override
	public ActionResult insertOrUpdate(Jarat entity, Integer foreignKey)
			throws NotConnectedException, EntityNotFoundException {
		checkConnected();
		/* Megkeresünk entity alapján 0 vagy 1 rekordot */
		try (PreparedStatement pstmt = connection.prepareStatement("SELECT * "
				+ "FROM AYC7B1.JARAT WHERE VONATSZAM LIKE ? ")) {
			pstmt.setInt(1, foreignKey);
			try (ResultSet rset = pstmt.executeQuery()) {
				/* Nem találtunk semmit */
				if (!rset.next()) {
					PreparedStatement insertStmt = connection.prepareStatement(" INSERT INTO AYC7B1.JARAT "
							+ " VALUES (?, ?, ?, TO_DATE(?, 'YY-MM-DD'), TO_DATE(?, 'YY-MM-DD'), ?)");
					insertStmt.setInt(1, entity.getVonatszam());
					insertStmt.setString(2, entity.getTipus());
					insertStmt.setString(3, entity.getNap());
					if (entity.getKezd() != null){
						insertStmt.setString(4, entity.getKezd().toString());
					} else {
						insertStmt.setString(4, null);
					}
					if (entity.getVege() != null){
						insertStmt.setString(5, entity.getVege().toString());
					} else {
						insertStmt.setString(5, null);
					}
					insertStmt.setString(6, entity.getMegjegyzes());
					insertStmt.executeQuery();
					return ActionResult.InsertOccurred;
				}
				/* Találtunk valamit */
				else {
					PreparedStatement updateStmt = connection.prepareStatement("UPDATE AYC7B1.JARAT "
							+ " SET TIPUS = ?, "
							+ " NAP = ?, "
							+ " KEZD = TO_DATE(?, 'YY-MM-DD'), "
							+ " VEGE = TO_DATE(?, 'YY-MM-DD'), "
							+ " MEGJEGYZES = ? "
							+ " WHERE VONATSZAM = ? ");
					/* Vagy módosítottunk, vagy nem */
					if (!entity.getTipus().equals("")) {
						updateStmt.setString(1, entity.getTipus());
					} else {
						updateStmt.setString(1, rset.getString("TIPUS"));
					}
					if (!entity.getNap().equals("")) {
						updateStmt.setString(2, entity.getNap());
					} else {
						updateStmt.setString(2, rset.getString("NAP"));
					}
					if (entity.getKezd() != null) {
						updateStmt.setString(3, entity.getKezd().toString());
					} else {
						updateStmt.setString(3, rset.getString("KEZD"));
					}
					if (entity.getVege() != null) {
						updateStmt.setString(4, entity.getVege().toString());
					} else {
						updateStmt.setString(4, rset.getString("VEGE"));
					}
					if (entity.getMegjegyzes() != null) {
						updateStmt.setString(5, entity.getMegjegyzes());
					} else {
						updateStmt.setString(5, rset.getString("MEGJEGYZES"));
					}
					updateStmt.setString(6, entity.getVonatszam().toString());
					updateStmt.executeUpdate();
					return ActionResult.UpdateOccurred;
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
			return ActionResult.ErrorOccurred;
		}
	}

	@Override
	public boolean setAutoCommit(boolean value) throws NotConnectedException {
		checkConnected();
		return false;
	}

	@Override
	public void disconnect() {
		
	}

	@Override
	public List<Person> sampleQuery() throws NotConnectedException {
		checkConnected();
		List<Person> result = new ArrayList<>();
		try (Statement stmt = connection.createStatement()) {
			try (ResultSet rset = stmt.executeQuery("SELECT nev, szemelyi_szam FROM OKTATAS.SZEMELYEK "
					+ "ORDER BY NEV "
					+ "OFFSET 0 ROWS FETCH NEXT 20 ROWS ONLY")){
				while (rset.next()) {
					Person p = new Person(rset.getString("nev"), rset.getString("szemelyi_szam"));
					result.add(p);
				}
				return result;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}





}
