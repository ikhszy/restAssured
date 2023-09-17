package api.utilities;

import java.io.IOException;

import org.testng.annotations.DataProvider;

public class DataProviders {

	@DataProvider(name="data")
	String[][] get_prog_data() throws IOException {
		String path = System.getProperty("user.dir") + "/TestData/testdata.xlsx";
		int rownum = XLUtility.getRowCount(path, "Sheet1");
		int colnum = XLUtility.getCellCount(path, "Sheet1", 1);
		
		String progdata[][] = new String[rownum][colnum];for (int i = 1; i <= rownum; i++) 
			{
				for (int j = 0; j < colnum; j++) 
					{
						progdata[i - 1][j] = XLUtility.getCellData(path, "Sheet1", i, j);
					}
			}
		return progdata;
	}
}
	
//	@DataProvider(name="getUserName")
//	public String[] getUserNames() throws IOException {
//		String path = System.getProperty("user.dir") + "//TestData//testdata.xlsx";
//		XLUtility xl = new XLUtility(path);
//		
//		int rownum = xl.getRowCount("Sheet1");
//		String apidata[] = new String [rownum];
//		
//		for(int i = 1; i <= rownum; i++) {
//			apidata[i-1] = xl.getCellData("Sheet1", i, 1);
//		}
//		
//		return apidata;
//	}
//}
