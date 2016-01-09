package cgeo.geocaching.connector.oc;

import static org.assertj.core.api.Assertions.assertThat;

import cgeo.CGeoTestCase;
import cgeo.geocaching.enumerations.LoadFlags;
import cgeo.geocaching.enumerations.LogType;
import cgeo.geocaching.models.Geocache;
import cgeo.geocaching.storage.DataStore;

public class OkapiClientTest extends CGeoTestCase {

    public static void testGetOCCache() {
        final String geoCode = "OU0331";
        Geocache cache = OkapiClient.getCache(geoCode);
        assertThat(cache).as("Cache from OKAPI").isNotNull();
        assert cache != null; // eclipse null analysis
        assertThat(cache.getGeocode()).isEqualTo(geoCode);
        assertThat(cache.getName()).isEqualTo("Oshkosh Municipal Tank");
        assertThat(cache.isDetailed()).isTrue();
        // cache should be stored to DB (to listID 0) when loaded above
        cache = DataStore.loadCache(geoCode, LoadFlags.LOAD_ALL_DB_ONLY);
        assert cache != null;
        assertThat(cache).isNotNull();
        assertThat(cache.getGeocode()).isEqualTo(geoCode);
        assertThat(cache.getName()).isEqualTo("Oshkosh Municipal Tank");
        assertThat(cache.isDetailed()).isTrue();
        assertThat(cache.getOwnerDisplayName()).isNotEmpty();
        assertThat(cache.getOwnerUserId()).isEqualTo(cache.getOwnerDisplayName());
    }

    public static void testOCSearchMustWorkWithoutOAuthAccessTokens() {
        final String geoCode = "OC1234";
        final Geocache cache = OkapiClient.getCache(geoCode);
        assertThat(cache).overridingErrorMessage("You must have a valid OKAPI key installed for running this test (but you do not need to set credentials in the app).").isNotNull();
        assert cache != null; // eclipse null analysis
        assertThat(cache.getName()).isEqualTo("Wupper-Schein");
    }

    public static void testOCCacheWithWaypoints() {
        final String geoCode = "OCDDD2";
        removeCacheCompletely(geoCode);
        Geocache cache = OkapiClient.getCache(geoCode);
        assertThat(cache).as("Cache from OKAPI").isNotNull();
        // cache should be stored to DB (to listID 0) when loaded above
        cache = DataStore.loadCache(geoCode, LoadFlags.LOAD_ALL_DB_ONLY);
        assert cache != null;
        assertThat(cache).isNotNull();
        assertThat(cache.getWaypoints()).hasSize(3);

        // load again
        cache.refreshSynchronous(null);
        assertThat(cache.getWaypoints()).hasSize(3);
    }

    public static void testOCWillAttendLogs() {
        final String geoCode = "OC6465";

        removeCacheCompletely(geoCode);
        final Geocache cache = OkapiClient.getCache(geoCode);
        assertThat(cache).as("Cache from OKAPI").isNotNull();
        assert cache != null; // eclipse null analysis
        assertThat(cache.getLogCounts().get(LogType.WILL_ATTEND)).isGreaterThan(0);
    }

    public static void testGetAllLogs() {
        final String geoCode = "OC10CB8";
        final Geocache cache = OkapiClient.getCache(geoCode);
        final int defaultLogCount = 10;
        assert cache != null; // eclipse null analysis
        assertThat(cache.getLogs().size()).isGreaterThan(defaultLogCount);
    }

    public static void testShortDescription() {
        final String geoCode = "OC10C06";
        final Geocache cache = OkapiClient.getCache(geoCode);
        assert cache != null; // eclipse null analysis
        assertThat(cache.getShortDescription()).isEqualTo("Nur in der fünften Jahreszeit kann er sprechen");
    }

}
