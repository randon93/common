package common.util;

import common.types.ByteNombreDto;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

@Slf4j
public final class GenerarKeyStore {

    public static final String TYPE = "jceks";
    public static final String SECRET_KEY = "RSA";
    public static final String EXTENSION = ".pfx";

    private GenerarKeyStore() {
    }

    public static ByteBuffer generarKeyStore(String passwordEntry, List<ByteNombreDto> data) {
        return generarKeyStore(passwordEntry, data, TipoKeyStore.JCEKS);
    }

    public static ByteBuffer generarKeyStoreEntry(String passwordEntry, List<ByteNombreDto> data) {
        log.info(String.format("el password es %s", passwordEntry));
        try {
            KeyStore ks = KeyStore.getInstance(TYPE);

            char[] passArray = passwordEntry.toCharArray();
            try {
                ks.load(null, passArray);


                for (ByteNombreDto bytes : data) {
                    SecretKey secretKey = new SecretKeySpec(bytes.getData().array(), SECRET_KEY);
                    KeyStore.SecretKeyEntry secret = new KeyStore.SecretKeyEntry(secretKey);
                    KeyStore.ProtectionParameter password = new KeyStore.PasswordProtection(new char[]{});
                    ks.setEntry(bytes.getNombre(), secret, password);
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ks.store(outputStream, passArray);
                ByteBuffer keyStoreData = ByteBuffer.wrap(outputStream.toByteArray());
                String temp = UtilFile.createFileTemp("test", EXTENSION, outputStream.toByteArray());
                log.info(String.format("la ubicacion del archivo temporal es = %s", temp));
                return keyStoreData;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (CertificateException e) {
                e.printStackTrace();
            }
            catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static ByteBuffer generarKeyStore(String passwordEntry, List<ByteNombreDto> data, TipoKeyStore tipo) {
        log.info(String.format("el password es %s", passwordEntry));
        try {
            KeyStore ks = KeyStore.getInstance(tipo.getTipo());

            char[] passArray = passwordEntry.toCharArray();
            try {
                ks.load(null, passArray);

                for (ByteNombreDto bytes : data) {

                    InputStream inputStream = new ByteArrayInputStream(bytes.getData().array());
                    try {
                        CertificateFactory cf = CertificateFactory.getInstance("X.509");
                        X509Certificate certificate = (X509Certificate) cf.generateCertificate(inputStream);
                        ks.setCertificateEntry(bytes.getNombre(), certificate);

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ks.store(outputStream, passArray);
                ByteBuffer keyStoreData = ByteBuffer.wrap(outputStream.toByteArray());
                String temp = UtilFile.createFileTemp("test", "." + tipo.getExtension(), outputStream.toByteArray());
                log.info(String.format("la ubicacion del archivo temporal es = %s", temp));
                return keyStoreData;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (CertificateException e) {
                e.printStackTrace();
            }
            catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ByteBuffer updateArchive(String passwordEntry, List<ByteNombreDto> data, ByteBuffer dataKeyStore) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(dataKeyStore.array());
            KeyStore ks = KeyStore.getInstance(TYPE);
            char[] passArray = passwordEntry.toCharArray();
            try {
                ks.load(inputStream, passArray);
                for (ByteNombreDto byteNombreDto : data) {
                    ks.deleteEntry(byteNombreDto.getNombre());
                }
                for (ByteNombreDto bytes : data) {
                    InputStream inputStreamCertificado = new ByteArrayInputStream(bytes.getData().array());
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    X509Certificate certificate = (X509Certificate) cf.generateCertificate(inputStream);
                    ks.setKeyEntry(bytes.getNombre(), new byte[]{}, new Certificate[]{certificate});
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ks.store(outputStream, passArray);
                ByteBuffer keyStoreData = ByteBuffer.wrap(outputStream.toByteArray());
                return keyStoreData;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (CertificateException e) {
                e.printStackTrace();
            }
            catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ByteBuffer updateArchiveEntry(String passwordEntry, List<ByteNombreDto> data, ByteBuffer dataKeyStore) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(dataKeyStore.array());
            KeyStore ks = KeyStore.getInstance(TYPE);
            char[] passArray = passwordEntry.toCharArray();
            try {
                ks.load(inputStream, passArray);
                for (ByteNombreDto byteNombreDto : data) {
                    ks.deleteEntry(byteNombreDto.getNombre());
                }
                for (ByteNombreDto bytes : data) {
                    SecretKey secretKey = new SecretKeySpec(bytes.getData().array(), SECRET_KEY);
                    KeyStore.SecretKeyEntry secret = new KeyStore.SecretKeyEntry(secretKey);
                    KeyStore.ProtectionParameter password = new KeyStore.PasswordProtection(new char[]{});
                    ks.setEntry(bytes.getNombre(), secret, password);
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ks.store(outputStream, passArray);
                ByteBuffer keyStoreData = ByteBuffer.wrap(outputStream.toByteArray());
                return keyStoreData;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (CertificateException e) {
                e.printStackTrace();
            }
            catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ByteBuffer deleteArchiveEntry(String passwordEntry, List<String> data, ByteBuffer dataKeyStore) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(dataKeyStore.array());
            KeyStore ks = KeyStore.getInstance(TYPE);
            char[] passArray = passwordEntry.toCharArray();
            try {
                ks.load(inputStream, passArray);
                for (String nombre : data) {
                    ks.deleteEntry(nombre);
                }
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ks.store(outputStream, passArray);
                ByteBuffer keyStoreData = ByteBuffer.wrap(outputStream.toByteArray());
                return keyStoreData;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (CertificateException e) {
                e.printStackTrace();
            }
            catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<ByteNombreDto> loadArchives(String passwordEntry, ByteBuffer dataKeyStore) {
        ArrayList listResponse = new ArrayList();

        try {
            String temp = UtilFile.createFileTemp("test", ".pfx", dataKeyStore.array());
            log.info(String.format("la ubicacion del archivo temporal es = %s", temp));
            ByteArrayInputStream inputStream = new ByteArrayInputStream(dataKeyStore.array());
            KeyStore ks = KeyStore.getInstance("jceks");
            char[] passArray = passwordEntry.toCharArray();

            try {
                ks.load(inputStream, passArray);
                Enumeration allAlias = ks.aliases();

                while (allAlias.hasMoreElements()) {
                    String alias = (String) allAlias.nextElement();
                    if (Objects.nonNull(alias)) {
                        Key key = ks.getKey(alias, new char[]{});
                        if (Objects.nonNull(key)) {
                            byte[] data = key.getEncoded();
                            listResponse.add(ByteNombreDto.builder().nombre(alias).data(ByteBuffer.wrap(data)).build());
                        }
                    }
                }
            }
            catch (IOException var11) {
                var11.printStackTrace();
            }
            catch (CertificateException var12) {
                var12.printStackTrace();
            }
            catch (NoSuchAlgorithmException var13) {
                var13.printStackTrace();
            }
            catch (UnrecoverableKeyException var14) {
                var14.printStackTrace();
            }
        }
        catch (KeyStoreException var15) {
            var15.printStackTrace();
        }

        return listResponse;
    }

    public static List<ByteNombreDto> loadArchivesPublic(String passwordEntry, ByteBuffer dataKeyStore) {
        List<ByteNombreDto> listResponse = new ArrayList<>();
        KeyStore ks = cargueKeyStore(passwordEntry, dataKeyStore);
        try {
            if (ks != null) {
                Enumeration<String> allAlias = ks.aliases();
                while (allAlias.hasMoreElements()) {
                    String alias = allAlias.nextElement();
                    if (Objects.nonNull(alias)) {
                        Certificate key = ks.getCertificate(alias);
                        if (Objects.nonNull(key)) {
                            byte[] data = key.getEncoded();
                            listResponse.add(
                                    ByteNombreDto
                                            .builder()
                                            .nombre(alias)
                                            .data(ByteBuffer.wrap(data))
                                            .build()
                            );
                        }
                    }
                }
            }
        }
        catch (KeyStoreException | CertificateException ex) {
            ex.printStackTrace();
        }
        return listResponse;
    }

    public static List<ByteNombreDto> loadArchivesPrivate(String passwordEntry, ByteBuffer dataKeyStore) {
        List<ByteNombreDto> listResponse = new ArrayList<>();
        KeyStore ks = cargueKeyStore(passwordEntry, dataKeyStore);
        try {
            if (ks != null) {
                Enumeration<String> allAlias = ks.aliases();
                while (allAlias.hasMoreElements()) {
                    String alias = allAlias.nextElement();
                    if (Objects.nonNull(alias)) {
                        Key key = ks.getKey(alias, passwordEntry.toCharArray());
                        if (Objects.nonNull(key)) {
                            byte[] data = key.getEncoded();
                            listResponse.add(
                                    ByteNombreDto
                                            .builder()
                                            .nombre(alias)
                                            .data(ByteBuffer.wrap(data))
                                            .build()
                            );
                        }
                    }
                }
            }
        }
        catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException e) {
            e.printStackTrace();
        }
        return listResponse;
    }

    private static KeyStore cargueKeyStore(String passwordEntry, ByteBuffer dataKeyStore) {
        String temp = UtilFile.createFileTemp("test", EXTENSION, dataKeyStore.array());
        log.info(String.format("la ubicacion del archivo temporal es = %s", temp));
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(dataKeyStore.array());
            KeyStore ks = KeyStore.getInstance("JKS");
            char[] passArray = passwordEntry.toCharArray();
            ks.load(inputStream, passArray);
            return ks;
        }
        catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            try {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(dataKeyStore.array());
                KeyStore ks = KeyStore.getInstance(TYPE);
                char[] passArray = passwordEntry.toCharArray();
                ks.load(inputStream, passArray);
                return ks;
            }
            catch (Exception ex) {
            }
        }
        return null;
    }

}
