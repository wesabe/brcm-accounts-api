package com.wesabe.api.util.money.tests;

import static org.junit.Assert.*;

import java.text.MessageFormat;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.util.money.CurrencyCodeParser;
import com.wesabe.api.util.money.UnknownCurrencyCodeException;

@RunWith(Enclosed.class)
public class CurrencyCodeParserTest {
	public static class Parsing_Outdated_Currency_Codes {
		
		@Test
		public void itMapsADFtoEUR() throws Exception {
			assertMapped("ADF", "EUR");
		}
		
		@Test
		public void itMapsMCFtoEUR() throws Exception {
			assertMapped("MCF", "EUR");
		}
		
		@Test
		public void itMapsVALtoEUR() throws Exception {
			assertMapped("VAL", "EUR");
		}
		
		@Test
		public void itMapsXEUtoEUR() throws Exception {
			assertMapped("XEU", "EUR");
		}
		
		@Test
		public void itMapsALKtoAFN() throws Exception {
			assertMapped("ALK", "AFN");
		}
		
		@Test
		public void itMapsAONtoAOA() throws Exception {
			assertMapped("AON", "AOA");
		}
		
		@Test
		public void itMapsAORtoAOA() throws Exception {
			assertMapped("AOR", "AOA");
		}
		
		@Test
		public void itMapsARMtoARS() throws Exception {
			assertMapped("ARM", "ARS");
		}
		
		@Test
		public void itMapsARLtoARS() throws Exception {
			assertMapped("ARL", "ARS");
		}
		
		@Test
		public void itMapsARPtoARS() throws Exception {
			assertMapped("ARP", "ARS");
		}
		
		@Test
		public void itMapsARAtoARS() throws Exception {
			assertMapped("ARA", "ARS");
		}
		
		@Test
		public void itMapsBGJtoBGN() throws Exception {
			assertMapped("BGJ", "BGN");
		}
		
		@Test
		public void itMapsBGKtoBGN() throws Exception {
			assertMapped("BGK", "BGN");
		}
		
		@Test
		public void itMapsBOPtoBOB() throws Exception {
			assertMapped("BOP", "BOB");
		}
		
		@Test
		public void itMapsBRBtoBRL() throws Exception {
			assertMapped("BRB", "BRL");
		}
		
		@Test
		public void itMapsBRCtoBRL() throws Exception {
			assertMapped("BRC", "BRL");
		}
		
		@Test
		public void itMapsBREtoBRL() throws Exception {
			assertMapped("BRE", "BRL");
		}
		
		@Test
		public void itMapsBRNtoBRL() throws Exception {
			assertMapped("BRN", "BRL");
		}
		
		@Test
		public void itMapsBRRtoBRL() throws Exception {
			assertMapped("BRR", "BRL");
		}
		
		@Test
		public void itMapsBRZtoBRL() throws Exception {
			assertMapped("BRZ", "BRL");
		}
		
		@Test
		public void itMapsCLEtoCLP() throws Exception {
			assertMapped("CLE", "CLP");
		}
		
		@Test
		public void itMapsCNXtoCNY() throws Exception {
			assertMapped("CNX", "CNY");
		}
		
		@Test
		public void itMapsCSJtoCZK() throws Exception {
			assertMapped("CSJ", "CZK");
		}
		
		@Test
		public void itMapsCSKtoCZK() throws Exception {
			assertMapped("CSK", "CZK");
		}
		
		@Test
		public void itMapsDDMtoDEM() throws Exception {
			assertMapped("DDM", "DEM");
		}
		
		@Test
		public void itMapsECStoUSD() throws Exception {
			assertMapped("ECS", "USD");
		}
		
		@Test
		public void itMapsEQEtoXAF() throws Exception {
			assertMapped("EQE", "XAF");
		}
		
		@Test
		public void itMapsESAtoESP() throws Exception {
			assertMapped("ESA", "ESP");
		}
		
		@Test
		public void itMapsESBtoESP() throws Exception {
			assertMapped("ESB", "ESP");
		}
		
		@Test
		public void itMapsGNEtoXOF() throws Exception {
			assertMapped("GNE", "XOF");
		}
		
		@Test
		public void itMapsILPtoILS() throws Exception {
			assertMapped("ILP", "ILS");
		}
		
		@Test
		public void itMapsILRtoILS() throws Exception {
			assertMapped("ILR", "ILS");
		}
		
		@Test
		public void itMapsISJtoISK() throws Exception {
			assertMapped("ISJ", "ISK");
		}
		
		@Test
		public void itMapsLAJtoLAK() throws Exception {
			assertMapped("LAJ", "LAK");
		}
		
		@Test
		public void itMapsMKNtoMKD() throws Exception {
			assertMapped("MKN", "MKD");
		}
		
		@Test
		public void itMapsMLFtoXOF() throws Exception {
			assertMapped("MLF", "XOF");
		}

		@Test
		public void itMapsMVQtoMVR() throws Exception {
			assertMapped("MVQ", "MVR");
		}

		@Test
		public void itMapsMXPtoMXN() throws Exception {
			assertMapped("MXP", "MXN");
		}

		@Test
		public void itMapsNFDtoCAD() throws Exception {
			assertMapped("NFD", "CAD");
		}

		@Test
		public void itMapsPEHtoPEN() throws Exception {
			assertMapped("PEH", "PEN");
		}

		@Test
		public void itMapsPEItoPEN() throws Exception {
			assertMapped("PEI", "PEN");
		}

		@Test
		public void itMapsPLZtoPLN() throws Exception {
			assertMapped("PLZ", "PLN");
		}

		@Test
		public void itMapsSURtoRUR() throws Exception {
			assertMapped("SUR", "RUR");
		}

		@Test
		public void itMapsTJRtoTJS() throws Exception {
			assertMapped("TJR", "TJS");
		}

		@Test
		public void itMapsUAKtoUAH() throws Exception {
			assertMapped("UAK", "UAH");
		}

		@Test
		public void itMapsUGStoUGX() throws Exception {
			assertMapped("UGS", "UGX");
		}

		@Test
		public void itMapsUYNtoUYU() throws Exception {
			assertMapped("UYN", "UYU");
		}

		@Test
		public void itMapsYDDtoYER() throws Exception {
			assertMapped("YDD", "YER");
		}

		@Test
		public void itMapsYUDtoCSD() throws Exception {
			assertMapped("YUD", "CSD");
		}

		@Test
		public void itMapsZRNtoCDF() throws Exception {
			assertMapped("ZRN", "CDF");
		}

		@Test
		public void itMapsZRZtoCDF() throws Exception {
			assertMapped("ZRZ", "CDF");
		}

		@Test
		public void itMapsZWCtoZWD() throws Exception {
			assertMapped("ZWC", "ZWD");
		}
		
		@Test
		public void itMapsYTLtoTRY() throws Exception {
			assertMapped("YTL", "TRY");
		}
		
		@Test
		public void itMapsAnEmptyStringToUSD() throws Exception {
			assertMapped("", "USD");
		}
		
		@Test
		public void itMapsVEBtoVEF() throws Exception {
			assertMapped("VEB", "VEF");
		}
		
		@Test
		public void itThrowsAnExceptionOnAnUnknownCurrencyCode() throws Exception {
			boolean exceptionThrown = false;
			String unknownCurrencyCode = null;
			String errorMessage = null;
			try {
				new CurrencyCodeParser().parse("GIBBERISH");
			} catch (UnknownCurrencyCodeException e) {
				exceptionThrown = true;
				unknownCurrencyCode = e.getCurrencyCode();
				errorMessage = e.getMessage();
			}
			assertTrue(exceptionThrown);
			assertEquals("GIBBERISH", unknownCurrencyCode);
			assertEquals("Unknown currency code: GIBBERISH", errorMessage);
		}
		
		private void assertMapped(final String oldCurrencyCode,
				final String modernCurrencyCode)
				throws UnknownCurrencyCodeException {
			assertEquals(
					MessageFormat.format("should map {0} to {1} but doesn''t",
							oldCurrencyCode, modernCurrencyCode),
					modernCurrencyCode,
					new CurrencyCodeParser().parse(oldCurrencyCode).getCurrencyCode()
			);
		}
		
	}
}
