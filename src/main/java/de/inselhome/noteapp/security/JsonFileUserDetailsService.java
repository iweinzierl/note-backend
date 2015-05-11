package de.inselhome.noteapp.security;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author  iweinzierl
 */
@Component
public class JsonFileUserDetailsService implements UserDetailsService {

    private final class GrantedAuthorityImpl implements GrantedAuthority {

        private final String authority;

        private GrantedAuthorityImpl(final String authority) {
            this.authority = authority;
        }

        @Override
        public String getAuthority() {
            return authority;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonFileUserDetailsService.class);

    @Value("${userService.file.location}")
    private String fileLocation;

    private final Map<String, User> users = Maps.newConcurrentMap();

    @PostConstruct
    public void setup() throws IOException {
        final Path storageFile = Paths.get(fileLocation);
        if (!storageFile.toFile().exists()) {
            throw new FileNotFoundException("Did not find a json user service file: " + fileLocation);
        }

        final JsonElement json = parseStorageFile(storageFile);
        convertToUsersMap(json);

        LOGGER.info("Loaded {} users from configuration '{}'", users.size(), fileLocation);
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return copy(users.get(username));
    }

    private User copy(final User user) {
        return new User(user.getUsername(), user.getPassword(), Lists.newArrayList(user.getAuthorities()));
    }

    private JsonElement parseStorageFile(final Path storageFile) throws IOException {
        final String storageContent = getFileContent(storageFile);
        final JsonElement storage = new JsonParser().parse(storageContent);

        if (storage == null) {
            throw new IllegalStateException("Storage file was not read or was empty");
        }

        return storage;
    }

    private String getFileContent(final Path file) throws IOException {
        return new String(Files.readAllBytes(file));
    }

    private void convertToUsersMap(final JsonElement jsonUsers) {
        final JsonArray users = jsonUsers.getAsJsonArray();

        for (JsonElement userElement : users) {
            final User user = convertToUser(userElement.getAsJsonObject());

            if (user != null) {
                this.users.put(user.getUsername(), user);
            }
        }
    }

    private User convertToUser(final JsonObject userObject) {
        return new User(userObject.get("name").getAsString(), userObject.get("password").getAsString(),
                convertAuthoritiesArrayToList(userObject.get("roles").getAsJsonArray()));
    }

    private List<GrantedAuthority> convertAuthoritiesArrayToList(final JsonArray authorities) {
        final List<GrantedAuthority> grantedAuthorities = Lists.newArrayList();

        for (final JsonElement authority : authorities) {
            grantedAuthorities.add(new GrantedAuthorityImpl(authority.getAsString()));
        }

        return grantedAuthorities;
    }
}
