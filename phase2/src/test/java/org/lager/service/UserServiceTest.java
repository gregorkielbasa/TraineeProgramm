package org.lager.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.exception.UserExistsAlreadyException;
import org.lager.exception.UserIllegalLoginException;
import org.lager.exception.UserIllegalPasswordException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service")
class UserServiceTest implements WithAssertions {

    @Mock
    private UserDetailsManager userManager;
    private UserService service;

    private PasswordEncoder encoder = new PasswordEncoder() {
        @Override
        public String encode(CharSequence rawPassword) {
            return rawPassword.toString();
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return encode(rawPassword).equals(encodedPassword);
        }
    };


    @Nested
    @DisplayName("creates a user")
    class CreateUserTest {

        @Test
        @DisplayName("and works")
        void properCase() throws UserExistsAlreadyException, UserIllegalPasswordException, UserIllegalLoginException {
            //Given
            String login = "user";
            String password = "pass";
            Mockito.when(userManager.userExists(any()))
                    .thenReturn(false);
            Mockito.doNothing().when(userManager).createUser(any());

            //When
            service = new UserService(userManager, encoder);
            service.createUser(login, password);

            //Then
            Mockito.verify(userManager).userExists(login);
            Mockito.verify(userManager).createUser(User.withUsername(login).password(password).roles("USER").build());
        }

        @Test
        @DisplayName("and throws an exception because login is invalid (blank)")
        void userIllegalNameBlank() throws UserExistsAlreadyException, UserIllegalPasswordException, UserIllegalLoginException {
            //Given
            String login = "";
            String password = "pass";

            //When
            service = new UserService(userManager, encoder);

            assertThatThrownBy(()->service.createUser(login, password))
                    .isInstanceOf(UserIllegalLoginException.class);

            //Then
        }

        @Test
        @DisplayName("and throws an exception because login is invalid (null)")
        void userIllegalNameNull() throws UserExistsAlreadyException, UserIllegalPasswordException, UserIllegalLoginException {
            //Given
            String login = null;
            String password = "pass";

            //When
            service = new UserService(userManager, encoder);

            assertThatThrownBy(()->service.createUser(login, password))
                    .isInstanceOf(UserIllegalLoginException.class);

            //Then
        }

        @Test
        @DisplayName("and throws an exception because password is invalid (blank)")
        void userIllegalPasswordBlank() throws UserExistsAlreadyException, UserIllegalPasswordException, UserIllegalLoginException {
            //Given
            String login = "user";
            String password = "";

            //When
            service = new UserService(userManager, encoder);

            assertThatThrownBy(()->service.createUser(login, password))
                    .isInstanceOf(UserIllegalPasswordException.class);

            //Then
        }

        @Test
        @DisplayName("and throws an exception because password is invalid (null)")
        void userIllegalPasswordNull() throws UserExistsAlreadyException, UserIllegalPasswordException, UserIllegalLoginException {
            //Given
            String login = "user";
            String password = null;

            //When
            service = new UserService(userManager, encoder);

            assertThatThrownBy(()->service.createUser(login, password))
                    .isInstanceOf(UserIllegalPasswordException.class);

            //Then
        }

        @Test
        @DisplayName("and throws an exception because user already exists")
        void userAlreadyExists() throws UserExistsAlreadyException, UserIllegalPasswordException, UserIllegalLoginException {
            //Given
            String login = "user";
            String password = "pass";
            Mockito.when(userManager.userExists(any()))
                    .thenReturn(true);

            //When
            service = new UserService(userManager, encoder);

            assertThatThrownBy(()->service.createUser(login, password))
                    .isInstanceOf(UserExistsAlreadyException.class);

            //Then
            Mockito.verify(userManager).userExists(login);
        }
    }

    @Nested
    @DisplayName("deletes a user")
    class deleteUserTest {

        @Test
        @DisplayName("and works")
        void properCase() throws UserExistsAlreadyException, UserIllegalPasswordException, UserIllegalLoginException {
            //Given
            String login = "user";
            Mockito.doNothing().when(userManager).deleteUser(any());

            //When
            service = new UserService(userManager, encoder);
            service.deleteUser(login);

            //Then
            Mockito.verify(userManager).deleteUser(login);
        }

        @Test
        @DisplayName("and throws an exception because login is invalid (blank)")
        void userIllegalNameBlank() throws UserExistsAlreadyException, UserIllegalPasswordException, UserIllegalLoginException {
            //Given
            String login = "";

            //When
            service = new UserService(userManager, encoder);

            assertThatThrownBy(()->service.deleteUser(login))
                    .isInstanceOf(UserIllegalLoginException.class);

            //Then
        }

        @Test
        @DisplayName("and throws an exception because login is invalid (null)")
        void userIllegalNameNull() throws UserExistsAlreadyException, UserIllegalPasswordException, UserIllegalLoginException {
            //Given
            String login = null;

            //When
            service = new UserService(userManager, encoder);

            assertThatThrownBy(()->service.deleteUser(login))
                    .isInstanceOf(UserIllegalLoginException.class);

            //Then
        }
    }
}