package jdbc_t;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class Jdbc_sqlite3 {

	public static void main(String[] args) {
		bejelentkez();
		Jdbc_sqlite3 prog = new Jdbc_sqlite3();
		prog.Reg();
		prog.Connect("bead.db");
		// prog.uj_termekek_tabla();
		// prog.uj_gyarak_tabla();
		Boolean ok = true;
		Scanner sc = new Scanner(System.in);
		do {

			System.out.println("=======================================");
			System.out.println(
					"Válasszon menüpontot:\n 1, Tábla kiírás\n 2, Új tábla létrehozása\n 3, Rekordok felvétele adott táblába.\n 4, Tábla törlése.\n 5, Tábla kiírása txt-be\n 6, Táblában rekord törlése\n 7, Meta adatok kiírása\n 8, Rekord módosítása\n 9, Kilépés");
			System.out.println("=======================================");
			switch (sc.nextInt()) {
			case 1:
				System.out.println(
						" 1, Egy megadott tábla kiírása.\n 2, Egy adott tábla kiírása megadott feltételek szerint.\n 3, Előre megírt lekérdezések\n 4, Kilépés");
				switch (sc.nextInt()) {
				case 1:
					prog.kiirTabla();
					break;
				case 2:
					prog.kiirTablaFelt();
					break;
				case 3:
					prog.eloreMegirtLekerdezesek();
					break;
				case 4:
					System.out.println("Kilépés...");
					break;
				default:
					System.out.println("Hibás input, kilépés...");
					break;
				}
				break;
			case 2:
				prog.ujTabla();
				break;
			case 3:
				prog.adatFelvesz();
				break;
			case 4:
				prog.tablaTorol();
				break;
			case 5:
				prog.TablaFileba();
				break;
			case 6:
				prog.elemTorol();
				break;
			case 7:
				System.out.println("1, Összes meta adat kiírása.\n2, Adott tábla.\n3, Kilépés.");
				switch (sc.nextInt()) {
				case 1:
					prog.metaDataKiir();
					break;
				case 2:
					prog.metaDataTabla();
				case 3:
					break;
				default:
					System.out.println("Hibás input, kilépés...");
					break;
				}

				break;
			case 8:
				prog.rekordModosit();
				break;
			case 9:
				System.out.println("Kilépés");
				ok = false;
				break;
			default:
				System.out.println("Hibás input");
				break;
			}
		} while (ok == true);

	}

	private void rekordModosit() {
		try {
			Connection conn = this.Connect("bead.db");
			Statement stmt = conn.createStatement();
			Scanner sc = new Scanner(System.in);
			System.out.println("Adja meg a tábla nevét:");
			String tNev = sc.nextLine();
			System.out.println("Adja meg a változtatni kívánt mezőt:");
			String adatok = sc.nextLine();
			System.out.println("Adja meg a rekord új értékét:");
			String uj = sc.nextLine();
			System.out.println("Adja meg a feltételt:");
			String felt = sc.nextLine();
			stmt.executeUpdate("UPDATE " + tNev + " SET " + adatok + " = " + uj + " WHERE " + felt);
			System.out.println("Adatok sikeresen cserélve");
			conn.close();

		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}

	private void metaDataTabla() {
		Connection connection = this.Connect("bead.db");
		DatabaseMetaData metaData = null;
		Scanner sc = new Scanner(System.in);
		System.out.println("Adja meg táblát:");
		String tNev = sc.nextLine();
		try {
			metaData = connection.getMetaData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			ResultSet columns = metaData.getColumns(null, null, tNev, null);
			while (columns.next()) {
				String columnName = columns.getString("COLUMN_NAME");
				String columnType = columns.getString("TYPE_NAME");
				System.out.println("Oszlop neve: " + columnName + ", Oszlop tipusa: " + columnType);
			}
			columns.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void metaDataKiir() {
		Connection connection = this.Connect("bead.db");
		DatabaseMetaData metaData = null;
		try {
			metaData = connection.getMetaData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			String databaseProductName = metaData.getDatabaseProductName();
			System.out.println("Adatbázis program neve: " + databaseProductName);

			String databaseProductVersion = metaData.getDatabaseProductVersion();
			System.out.println("Adatbázis program verziója: " + databaseProductVersion);

			String url = metaData.getURL();
			System.out.println("Adatbázis URL: " + url);

			ResultSet tables = metaData.getTables(null, null, null, null);
			while (tables.next()) {
				String tableName = tables.getString("TABLE_NAME");
				System.out.println("Tábla Neve: " + tableName);
			}
			tables.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void elemTorol() {
		Scanner sc = new Scanner(System.in);
		Connection conn = this.Connect("bead.db");
		System.out.println("Adja meg melyik táblából szeretne elemet törölni:");
		String tNev = sc.nextLine();
		System.out.println("Adja meg a törölni kívánt táblának a PRIMARY KEY-ének nevét:");
		String kulcs = sc.nextLine();
		System.out.print("Adja meg a törölni kívánt rekord kulcsát: ");
		int id = sc.nextInt();
		sc.nextLine();
		PreparedStatement preparedStatement = null;
		try {

			String sql = "DELETE FROM " + tNev + " WHERE " + kulcs + "=?";
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, id);
			int rows = preparedStatement.executeUpdate();

			if (rows > 0) {
				System.out.println("Adat sikeresen törölve!");
			} else {
				System.out.println("Nem sikerült adatot törölni.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	private void tablaTorol() {
		try {
			Connection conn = this.Connect("bead.db");
			Statement stmt = conn.createStatement();
			Scanner sc = new Scanner(System.in);
			System.out.println("Adja meg a törölni kívánt tábla nevét:");
			String tNev = sc.nextLine();
			stmt.executeUpdate("DROP TABLE " + tNev);
			System.out.println("Tábla sikeresen törölve.");
			conn.close();

		} catch (Exception ee) {
			ee.printStackTrace();
		}

	}

	private void adatFelvesz() {
		try {
			Connection conn = this.Connect("bead.db");
			Statement stmt = conn.createStatement();
			Scanner sc = new Scanner(System.in);
			System.out.println("Adja meg a tábla nevét:");
			String tNev = sc.nextLine();
			System.out.println("Adja meg a bevinni kívánt adatokat:");
			String adatok = sc.nextLine();
			stmt.executeUpdate("INSERT INTO " + tNev + " VALUES (" + adatok + ")");
			System.out.println("Adatok sikeresen felvéve");
			conn.close();

		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}

	private static void bejelentkez() {
		String felhasznalo = "c7h5vb";
		String jelszo = "c7h5vb";
		Boolean ok = false;
		Scanner sc = new Scanner(System.in);
		do {
			System.out.println("Adja meg a felhasználó nevét: ");
			if (felhasznalo.equals(sc.nextLine())) {
				System.out.println("Adja meg jelszavát: ");
				if (jelszo.equals(sc.nextLine())) {
					ok = true;
					System.out.println("Sikeres bejelentkezés!");
				} else {
					System.out.println("Hibás jelszó!");
				}
			} else {
				System.out.println("Hibás felhasználónév! ");
			}
		} while (ok == false);

	}

	public void Reg() {
		try {
			Class.forName("org.sqlite.JDBC");
			System.out.println("Sikeres Driver regisztrálás!");
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	public Connection Connect(String string) {
		Connection conn = null;
		String url = "jdbc:sqlite:C:/sql3/" + string;
		try {
			conn = DriverManager.getConnection(url);
			// System.out.println("Létrejött a kapcsolat az SQLite-tal.");
			return conn;

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return conn;
		}

	}

	public void uj_termekek_tabla() {

		try {
			Connection conn = this.Connect("bead.db");
			Statement stmt = conn.createStatement();
			String par = "CREATE TABLE Termekek (Vallalatiazonosito INT,Bevetel NUMBER(10), Marketing NUMBER(10), Otletek NUMBER(10), Teszteles NUMBER(10), Fajtaja VARCHAR(5), PRIMARY KEY (Vallalatiazonosito))";
			// stmt.execute(par);
			stmt.executeUpdate("INSERT INTO Termekek VALUES (1, 11000, 1000, 100, 200, 'Hell')");
			stmt.executeUpdate("INSERT INTO Termekek VALUES (2, 12000, 2000, 300, 300, 'Xixo')");
			stmt.executeUpdate("INSERT INTO Termekek VALUES (3, 13000, 3000, 500, 120, 'Egyéb')");
			conn.close();
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}

	public void uj_gyarak_tabla() {

		try {
			Connection conn = this.Connect("bead.db");
			Statement stmt = conn.createStatement();
			String par = "CREATE TABLE Gyarak (Gyarazonosito INT, Iranyitoszam NUMBER(4), Varos VARCHAR(30), Utca VARCHAR(30), Kiadasok NUMBER(10), Gyartasirata NUMBER(11), Vallalatiazonosito INT, PRIMARY KEY (Gyarazonosito), FOREIGN KEY (Vallalatiazonosito) REFERENCES Termekek(Vallalatiazonosito))";
			// stmt.execute(par);
			stmt.executeUpdate("INSERT INTO Gyarak VALUES (11, 3535, 'Miskolc', 'Gőz út', 100, 10, 1)");
			stmt.executeUpdate("INSERT INTO Gyarak VALUES (22, 3522, 'Miskolc', 'Kandó Kálmán útca', 200, 30, 2)");
			stmt.executeUpdate("INSERT INTO Gyarak VALUES (33, 2234, 'Budapest', 'Nagy útca', 300, 20, 3)");
			conn.close();
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}

	public void TablaFileba() {
		try {
			System.out.println("Adja meg a txt-be kiírandó táblát:");
			Scanner sc = new Scanner(System.in);
			String tabla = sc.nextLine();
			Connection conn = this.Connect("bead.db");
			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery("SELECT * FROM " + tabla);

			FileWriter fileWriter = new FileWriter("tablak.txt");
			ResultSetMetaData rsmd = resultSet.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			fileWriter.write("Tábla:" + tabla + "\n");
			while (resultSet.next()) {
				for (int i = 1; i < columnsNumber; i++) {
					String ertek = resultSet.getString(i);
					fileWriter.write("/" + ertek + "/");
				}
				fileWriter.write("\n");
			}

			fileWriter.close();
			resultSet.close();
			stmt.close();
			conn.close();

			System.out.println("Az adatok sikeresen lelettek mentve a tablak.txt-be.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void kiirTabla() {
		try {
			Scanner sc = new Scanner(System.in);
			System.out.println("Adja meg a Tábla nevét.");
			String tabla = sc.nextLine();
			Connection conn = this.Connect("bead.db");
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + tabla);
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
					if (i > 1)
						System.out.print(",  ");
					String columnValue = rs.getString(i);
					System.out.print(columnValue + " " + rsmd.getColumnName(i));
				}
				System.out.println("");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void kiirTablaFelt() {
		try {
			Connection conn = this.Connect("bead.db");
			Statement stmt = conn.createStatement();
			Scanner sc = new Scanner(System.in);
			System.out.println("Adja meg a Tábla nevét:");
			String tabla = sc.nextLine();
			System.out.println("Adja meg a feltételt:");
			String felt = sc.nextLine();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + tabla + " WHERE " + felt);
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
					if (i > 1)
						System.out.print(",  ");
					String columnValue = rs.getString(i);
					System.out.print(columnValue + " " + rsmd.getColumnName(i));
				}
				System.out.println("");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void ujTabla() {

		try {
			Connection conn = this.Connect("bead.db");
			Statement stmt = conn.createStatement();
			Scanner sc = new Scanner(System.in);
			System.out.println("Adja meg az új tábla nevét:");
			String tNev = sc.nextLine();
			System.out.println("Adja meg a tábla argumentumait:");
			String args = sc.nextLine();
			String par = "CREATE TABLE " + tNev + "(" + args + ")";
			stmt.execute(par);
			System.out.println("Tábla sikeresen létrehozva!");
			conn.close();

		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}

	public void eloreMegirtLekerdezesek() {
		try {
			Connection conn = this.Connect("bead.db");
			Statement stmt = conn.createStatement();
			Scanner sc = new Scanner(System.in);
			System.out.println("1, Legnagyobb gyártású rátájú gyár\n2, Hell termékek átlagos bevétele");
			switch (sc.nextInt()) {
			case 1:
				ResultSet rs = stmt.executeQuery("SELECT Gyarazonosito,MAX(Gyartasirata) FROM Gyarak");
				System.out.println(rs.getString(1));
				break;
			case 2:
				ResultSet rs2 = stmt.executeQuery("SELECT AVG(Bevetel) FROM Termekek");
				System.out.println(rs2.getString(1));
				break;
			}

		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}
}
