package ee.hm.dop.config;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationDecoder;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.interpol.Lookup;
import org.apache.commons.configuration2.sync.LockMode;
import org.apache.commons.configuration2.sync.Synchronizer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
public class ConfigurationImpl implements Configuration {

    @Inject
    private Environment env;

    @Override
    public Configuration subset(String s) {
        return null;
    }

    @Override
    public void addProperty(String s, Object o) {

    }

    @Override
    public void setProperty(String s, Object o) {

    }

    @Override
    public void clearProperty(String s) {

    }

    @Override
    public void clear() {

    }

    @Override
    public ConfigurationInterpolator getInterpolator() {
        return null;
    }

    @Override
    public void setInterpolator(ConfigurationInterpolator configurationInterpolator) {

    }

    @Override
    public void installInterpolator(Map<String, ? extends Lookup> map, Collection<? extends Lookup> collection) {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean containsKey(String s) {
        return false;
    }

    @Override
    public Object getProperty(String s) {
        return null;
    }

    @Override
    public Iterator<String> getKeys(String s) {
        return null;
    }

    @Override
    public Iterator<String> getKeys() {
        return null;
    }

    @Override
    public Properties getProperties(String s) {
        return null;
    }

    @Override
    public boolean getBoolean(String s) {
        return false;
    }

    @Override
    public boolean getBoolean(String s, boolean b) {
        return false;
    }

    @Override
    public Boolean getBoolean(String s, Boolean aBoolean) {
        return null;
    }

    @Override
    public byte getByte(String s) {
        return 0;
    }

    @Override
    public byte getByte(String s, byte b) {
        return 0;
    }

    @Override
    public Byte getByte(String s, Byte aByte) {
        return null;
    }

    @Override
    public double getDouble(String s) {
        return 0;
    }

    @Override
    public double getDouble(String s, double v) {
        return 0;
    }

    @Override
    public Double getDouble(String s, Double aDouble) {
        return null;
    }

    @Override
    public float getFloat(String s) {
        return 0;
    }

    @Override
    public float getFloat(String s, float v) {
        return 0;
    }

    @Override
    public Float getFloat(String s, Float aFloat) {
        return null;
    }

    @Override
    public int getInt(String s) {
        String property = env.getProperty(s);
        return property != null ? Integer.valueOf(property) : 0;
    }

    @Override
    public int getInt(String s, int i) {
        return 0;
    }

    @Override
    public Integer getInteger(String s, Integer integer) {
        return null;
    }

    @Override
    public long getLong(String s) {
        return 0;
    }

    @Override
    public long getLong(String s, long l) {
        return 0;
    }

    @Override
    public Long getLong(String s, Long aLong) {
        return null;
    }

    @Override
    public short getShort(String s) {
        return 0;
    }

    @Override
    public short getShort(String s, short i) {
        return 0;
    }

    @Override
    public Short getShort(String s, Short aShort) {
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(String s) {
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(String s, BigDecimal bigDecimal) {
        return null;
    }

    @Override
    public BigInteger getBigInteger(String s) {
        return null;
    }

    @Override
    public BigInteger getBigInteger(String s, BigInteger bigInteger) {
        return null;
    }

    @Override
    public String getString(String s) {
        return env.getProperty(s);
    }

    @Override
    public String getString(String s, String s1) {
        return null;
    }

    @Override
    public String getEncodedString(String s, ConfigurationDecoder configurationDecoder) {
        return null;
    }

    @Override
    public String getEncodedString(String s) {
        return null;
    }

    @Override
    public String[] getStringArray(String s) {
        return new String[0];
    }

    @Override
    public List<Object> getList(String s) {
        return null;
    }

    @Override
    public List<Object> getList(String s, List<?> list) {
        return null;
    }

    @Override
    public <T> T get(Class<T> aClass, String s) {
        return null;
    }

    @Override
    public <T> T get(Class<T> aClass, String s, T t) {
        return null;
    }

    @Override
    public Object getArray(Class<?> aClass, String s) {
        return null;
    }

    @Override
    public Object getArray(Class<?> aClass, String s, Object o) {
        return null;
    }

    @Override
    public <T> List<T> getList(Class<T> aClass, String s) {
        return null;
    }

    @Override
    public <T> List<T> getList(Class<T> aClass, String s, List<T> list) {
        return null;
    }

    @Override
    public <T> Collection<T> getCollection(Class<T> aClass, String s, Collection<T> collection) {
        return null;
    }

    @Override
    public <T> Collection<T> getCollection(Class<T> aClass, String s, Collection<T> collection, Collection<T> collection1) {
        return null;
    }

    @Override
    public ImmutableConfiguration immutableSubset(String s) {
        return null;
    }

    @Override
    public Synchronizer getSynchronizer() {
        return null;
    }

    @Override
    public void setSynchronizer(Synchronizer synchronizer) {

    }

    @Override
    public void lock(LockMode lockMode) {

    }

    @Override
    public void unlock(LockMode lockMode) {

    }
}
