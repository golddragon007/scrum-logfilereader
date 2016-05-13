package hu.bme.tmit.agile.logfilereader;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import hu.bme.tmit.agile.logfilereader.model.LogTimestamp;

public class LogTimeStampTest {

	private LogTimestamp lt0 = new LogTimestamp(2016, 5, 6, 13, 33, 35, 888);
	private LogTimestamp lt1 = new LogTimestamp(2016, 5, 6, 12, 33, 35, 888);
	private LogTimestamp lt2 = new LogTimestamp(2016, 5, 6, 12, 33, 35, 888);
	private LogTimestamp lt3 = new LogTimestamp(2016, 5, 6, 12, 33, 35, 889);

	@Test
	public void isEqualTest() {
		assertTrue(lt1.isEqual(lt2));
	}

	@Test
	public void isAfterTestSameDate() {
		assertTrue(lt3.isAfter(lt2));
	}

	@Test
	public void isBeforeTestSameDate() {
		assertTrue(lt2.isBefore(lt3));
	}

	@Test
	public void isAfterTestNotSameDate() {
		assertTrue(lt0.isAfter(lt3));
	}

	@Test
	public void isBeforeTestNotSameDate() {
		assertTrue(lt3.isBefore(lt0));
	}
}
