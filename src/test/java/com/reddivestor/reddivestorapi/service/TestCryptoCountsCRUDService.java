package com.reddivestor.reddivestorapi.service;

import com.reddivestor.reddivestorapi.models.Crypto;
import com.reddivestor.reddivestorapi.persist.ReadWriteDatastore;
import com.reddivestor.reddivestorapi.service.cryptocounts.CryptoTimeBucketCRUDService;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.*;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class TestCryptoCountsCRUDService {

    ReadWriteDatastore mockReadWriteDatastore;
    CryptoTimeBucketCRUDService cryptoCountsCRUDService;

    @Before
    public void init() {
        //Init the Crypto service with a mock.
        mockReadWriteDatastore = mock(ReadWriteDatastore.class);
        cryptoCountsCRUDService = new CryptoTimeBucketCRUDService(mockReadWriteDatastore);
    }
    @Rule
    public ExpectedException EXCEPTION_RULE = ExpectedException.none();

    @Test
    public void testGetByTimeBucketHappyPath() {
        List<Crypto> mockCryptoList = Arrays.asList(
                new Crypto("blabla crypto",
                        3,
                        1),
                new Crypto("blabla crypto2",
                        2,
                        2),
                new Crypto("blabla crypto3",
                        1,
                        3)
        );

        Mockito.when(mockReadWriteDatastore.findByTimestampBetween(any(Date.class), any(Date.class)))
                .thenReturn(mockCryptoList);
        try {
            assertTrue(cryptoCountsCRUDService.getByTimeBucket(
                    LocalDateTime.now().minusDays(6), LocalDateTime.now())
                    .equals(mockCryptoList));
        } catch (Exception ex) {
            //Test failed.
        }
    }

    @Test
    public void testGetByTimeBucketNullStartTimeBucket() throws Exception{
        EXCEPTION_RULE.expect(Exception.class);
        EXCEPTION_RULE.expectMessage("timeBucket parameter was null or empty.");
        cryptoCountsCRUDService.getByTimeBucket(null, LocalDateTime.now());
    }

    @Test
    public void testGetByTimeBucketNullEndTimeBucket() throws Exception{
        EXCEPTION_RULE.expect(Exception.class);
        EXCEPTION_RULE.expectMessage("timeBucket parameter was null or empty.");
        cryptoCountsCRUDService.getByTimeBucket(LocalDateTime.now().minusDays(5), null);
    }

    @Test
    public void testGetTopCoinsByTimeBucket() throws Exception {
        List<Crypto> mockCryptoList = Arrays.asList(
                new Crypto("blabla crypto",
                        3,
                        1),
                new Crypto("blabla crypto2",
                        2,
                        2),
                new Crypto("blabla crypto3",
                        1,
                        3)
        );
        Mockito.when(mockReadWriteDatastore.findByTimestampBetween(any(Date.class), any(Date.class)))
                .thenReturn(mockCryptoList);
        try {
            List<Crypto> cryptoList = cryptoCountsCRUDService.getTopCoinNamesByTimeBucket(
                    LocalDateTime.now().minusDays(6), LocalDateTime.now());
            assertTrue(cryptoList.size() == 3);
        } catch (Exception ex) {
            //Test failed.
        }
    }

    @Test
    public void testGetTopCoinsByTimeBucketNullStartTimeBucket() throws Exception{
        EXCEPTION_RULE.expect(Exception.class);
        EXCEPTION_RULE.expectMessage("timeBucket parameter was null or empty.");
        cryptoCountsCRUDService.getTopCoinNamesByTimeBucket(null, LocalDateTime.now());
    }

    @Test
    public void testGetTopCoinsByTimeBucketNullEndTimeBucket() throws Exception{
        EXCEPTION_RULE.expect(Exception.class);
        EXCEPTION_RULE.expectMessage("timeBucket parameter was null or empty.");
        cryptoCountsCRUDService.getTopCoinNamesByTimeBucket(LocalDateTime.now().minusDays(5), null);
    }
}
