package ua.kpi.iot_lighting

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import ua.kpi.iot_lighting.data.AuthValidator
import ua.kpi.iot_lighting.data.UserProfile

class AuthValidatorTest {

    @Test
    fun testValidateLoginValid() {
        val result = AuthValidator.validateLogin("student@kpi.ua", "password123")
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun testValidateLoginInvalidEmail() {
        val blankEmail = AuthValidator.validateLogin("", "password123")
        assertFalse(blankEmail.isValid)
        assertNotNull(blankEmail.errorMessage)

        val noAt = AuthValidator.validateLogin("student.kpi.ua", "password123")
        assertFalse(noAt.isValid)

        val noDot = AuthValidator.validateLogin("student@kpi", "password123")
        assertFalse(noDot.isValid)
    }

    @Test
    fun testValidateLoginShortPassword() {
        val shortPass = AuthValidator.validateLogin("student@kpi.ua", "12345")
        assertFalse(shortPass.isValid)
        assertTrue(shortPass.errorMessage?.contains("6") == true)
    }

    @Test
    fun testValidateRegistrationValid() {
        val result = AuthValidator.validateRegistration(
            name = "Студент-Інженер",
            group = "ТВ-13",
            email = "student@kpi.ua",
            password = "password123",
            confirmPassword = "password123"
        )
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun testValidateRegistrationBlankName() {
        val result = AuthValidator.validateRegistration(
            name = " ",
            group = "ТВ-13",
            email = "student@kpi.ua",
            password = "password123",
            confirmPassword = "password123"
        )
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("ПІБ") == true)
    }

    @Test
    fun testValidateRegistrationBlankGroup() {
        val result = AuthValidator.validateRegistration(
            name = "Студент-Інженер",
            group = "",
            email = "student@kpi.ua",
            password = "password123",
            confirmPassword = "password123"
        )
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("групу") == true)
    }

    @Test
    fun testValidateRegistrationMismatchedPasswords() {
        val result = AuthValidator.validateRegistration(
            name = "Студент-Інженер",
            group = "ТВ-13",
            email = "student@kpi.ua",
            password = "password123",
            confirmPassword = "password321"
        )
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("співпадають") == true)
    }

    @Test
    fun testUserProfileModelDefaultsAndAliases() {
        val profile = UserProfile(
            id = "user_1",
            name = "Студент-Інженер",
            group = "ТВ-13",
            email = "student@kpi.ua",
            password = "password123"
        )
        assertEquals("user_1", profile.id)
        assertEquals("Студент-Інженер", profile.name)
        assertEquals("Студент-Інженер", profile.fullName)
        assertEquals("ТВ-13", profile.group)
        assertEquals("ТВ-13", profile.studentGroup)
        assertEquals("student@kpi.ua", profile.email)
        assertEquals("password123", profile.password)
        assertEquals("Інженер IoT-систем", profile.role)
        assertEquals("13", profile.variant)
    }
}
