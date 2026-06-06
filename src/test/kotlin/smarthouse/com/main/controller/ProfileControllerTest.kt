package smarthouse.com.main.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import smarthouse.com.main.model.Profile
import smarthouse.com.main.repository.ProfileRepository
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * Testes unitários para ProfileController
 *
 * Objetivo: Testar operações CRUD de perfis de usuário com diferentes permissões
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("ProfileController - Gerenciamento de Perfis de Usuário")
class ProfileControllerTest {

    @Mock
    private lateinit var profileRepository: ProfileRepository

    private lateinit var profileController: ProfileController

    private lateinit var testProfile: Profile

    @BeforeEach
    fun setUp() {
        profileController = ProfileController(profileRepository)

        // Mock utilizado para simular retorno do repositório
        testProfile = Profile(
            id = 1L,
            name = "ADMIN",
            description = "Administrador do sistema",
            canControlDevices = true,
            canEditStructure = true,
            canViewLogs = true
        )
    }

    @Nested
    @DisplayName("Operações de Listagem")
    inner class ListagensTests {

        @Test
        @DisplayName("deveListarTodosPerfis")
        fun deveListarTodosPerfis() {
            // Given
            val perfis = listOf(testProfile)
            whenever(profileRepository.findAll()).thenReturn(perfis)

            // When
            val resultado = profileController.list()

            // Then
            assertEquals(1, resultado.size)
            assertEquals("ADMIN", resultado[0].name)
            assertEquals("Administrador do sistema", resultado[0].description)
            verify(profileRepository).findAll()
        }

        @Test
        @DisplayName("deveListarPerfilsVazio")
        fun deveListarPerfilsVazio() {
            // Given
            whenever(profileRepository.findAll()).thenReturn(emptyList())

            // When
            val resultado = profileController.list()

            // Then
            assertEquals(0, resultado.size)
            verify(profileRepository).findAll()
        }

        @Test
        @DisplayName("deveListarMultiplosPerfis")
        fun deveListarMultiplosPerfis() {
            // Given
            val perfil2 = Profile(
                id = 2L,
                name = "MORADOR",
                description = "Morador da casa",
                canControlDevices = true,
                canEditStructure = false,
                canViewLogs = true
            )
            val perfil3 = Profile(
                id = 3L,
                name = "VISITANTE",
                description = "Visitante",
                canControlDevices = false,
                canEditStructure = false,
                canViewLogs = false
            )
            val perfis = listOf(testProfile, perfil2, perfil3)
            whenever(profileRepository.findAll()).thenReturn(perfis)

            // When
            val resultado = profileController.list()

            // Then
            assertEquals(3, resultado.size)
            assertEquals("ADMIN", resultado[0].name)
            assertEquals("MORADOR", resultado[1].name)
            assertEquals("VISITANTE", resultado[2].name)
        }

        @Test
        @DisplayName("deveListarPerfilComTodasPermissoes")
        fun deveListarPerfilComTodasPermissoes() {
            // Given
            whenever(profileRepository.findAll()).thenReturn(listOf(testProfile))

            // When
            val resultado = profileController.list()

            // Then
            assertTrue(resultado[0].canControlDevices)
            assertTrue(resultado[0].canEditStructure)
            assertTrue(resultado[0].canViewLogs)
        }

        @Test
        @DisplayName("deveListarPerfilSemPermissoes")
        fun deveListarPerfilSemPermissoes() {
            // Given
            val perfilRestrito = Profile(
                id = 1L,
                name = "RESTRITO",
                description = "Perfil restrito",
                canControlDevices = false,
                canEditStructure = false,
                canViewLogs = false
            )
            whenever(profileRepository.findAll()).thenReturn(listOf(perfilRestrito))

            // When
            val resultado = profileController.list()

            // Then
            assertFalse(resultado[0].canControlDevices)
            assertFalse(resultado[0].canEditStructure)
            assertFalse(resultado[0].canViewLogs)
        }
    }

    @Nested
    @DisplayName("Operações de Criação")
    inner class CriacaoTests {

        @Test
        @DisplayName("deveCriarUmPerfil")
        fun deveCriarUmPerfil() {
            // Given
            whenever(profileRepository.save(any())).thenReturn(testProfile)

            // When
            val resultado = profileController.create(testProfile)

            // Then
            assertNotNull(resultado)
            assertEquals("ADMIN", resultado.name)
            assertTrue(resultado.canControlDevices)
            verify(profileRepository).save(any())
        }

        @Test
        @DisplayName("deveCriarPerfilComPermissoesVariadas")
        fun deveCriarPerfilComPermissoesVariadas() {
            // Given
            val perfilMorador = Profile(
                id = null,
                name = "MORADOR",
                description = "Morador",
                canControlDevices = true,
                canEditStructure = false,
                canViewLogs = true
            )
            whenever(profileRepository.save(any())).thenReturn(perfilMorador)

            // When
            val resultado = profileController.create(perfilMorador)

            // Then
            assertTrue(resultado.canControlDevices)
            assertFalse(resultado.canEditStructure)
            assertTrue(resultado.canViewLogs)
        }

        @Test
        @DisplayName("deveCriarPerfilVisitante")
        fun deveCriarPerfilVisitante() {
            // Given
            val perfilVisitante = Profile(
                id = null,
                name = "VISITANTE",
                description = "Visitante",
                canControlDevices = false,
                canEditStructure = false,
                canViewLogs = false
            )
            whenever(profileRepository.save(any())).thenReturn(perfilVisitante)

            // When
            val resultado = profileController.create(perfilVisitante)

            // Then
            assertFalse(resultado.canControlDevices)
            assertFalse(resultado.canEditStructure)
            assertFalse(resultado.canViewLogs)
        }

        @Test
        @DisplayName("deveCriarMultiplosPerfis")
        fun deveCriarMultiplosPerfis() {
            // Given
            val perfil1 = Profile(1L, "ADMIN", "Admin", true, true, true)
            val perfil2 = Profile(2L, "USER", "User", true, false, true)
            whenever(profileRepository.save(any()))
                .thenReturn(perfil1)
                .thenReturn(perfil2)

            // When
            val res1 = profileController.create(perfil1)
            val res2 = profileController.create(perfil2)

            // Then
            assertEquals("ADMIN", res1.name)
            assertEquals("USER", res2.name)
        }
    }

    @Nested
    @DisplayName("Operações de Atualização")
    inner class AtualizacaoTests {

        @Test
        @DisplayName("deveAtualizarUmPerfil")
        fun deveAtualizarUmPerfil() {
            // Given
            val perfilAtualizado = Profile(
                id = 1L,
                name = "ADMIN_UPDATED",
                description = "Administrador Atualizado",
                canControlDevices = true,
                canEditStructure = true,
                canViewLogs = true
            )
            whenever(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile))
            whenever(profileRepository.save(any())).thenReturn(perfilAtualizado)

            // When
            val resultado = profileController.update(1L, perfilAtualizado)

            // Then
            assertNotNull(resultado)
            assertEquals("ADMIN_UPDATED", resultado.name)
            verify(profileRepository).findById(1L)
            verify(profileRepository).save(any())
        }

        @Test
        @DisplayName("deveAtualizarPermissoes")
        fun deveAtualizarPermissoes() {
            // Given
            val perfilComPermissoesAlteradas = Profile(
                id = 1L,
                name = testProfile.name,
                description = testProfile.description,
                canControlDevices = false,
                canEditStructure = false,
                canViewLogs = true
            )
            whenever(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile))
            whenever(profileRepository.save(any())).thenReturn(perfilComPermissoesAlteradas)

            // When
            val resultado = profileController.update(1L, perfilComPermissoesAlteradas)

            // Then
            assertFalse(resultado.canControlDevices)
            assertFalse(resultado.canEditStructure)
            assertTrue(resultado.canViewLogs)
        }


        @Test
        @DisplayName("deveAtualizarDescricaoPerfil")
        fun deveAtualizarDescricaoPerfil() {
            // Given
            val perfilComNovaDescricao = Profile(
                id = 1L,
                name = testProfile.name,
                description = "Nova descrição",
                canControlDevices = testProfile.canControlDevices,
                canEditStructure = testProfile.canEditStructure,
                canViewLogs = testProfile.canViewLogs
            )
            whenever(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile))
            whenever(profileRepository.save(any())).thenReturn(perfilComNovaDescricao)

            // When
            val resultado = profileController.update(1L, perfilComNovaDescricao)

            // Then
            assertEquals("Nova descrição", resultado.description)
        }
    }

    @Nested
    @DisplayName("Operações de Deleção")
    inner class DelecaoTests {

        @Test
        @DisplayName("deveDeletarUmPerfil")
        fun deveDeletarUmPerfil() {
            // When
            profileController.delete(1L)

            // Then
            verify(profileRepository).deleteById(1L)
        }

        @Test
        @DisplayName("deveDeletarMultiplosPerfis")
        fun deveDeletarMultiplosPerfis() {
            // When
            profileController.delete(1L)
            profileController.delete(2L)
            profileController.delete(3L)

            // Then
            verify(profileRepository).deleteById(1L)
            verify(profileRepository).deleteById(2L)
            verify(profileRepository).deleteById(3L)
        }

        @Test
        @DisplayName("deveDeletarPerfilComIdGrande")
        fun deveDeletarPerfilComIdGrande() {
            // When
            profileController.delete(999999L)

            // Then
            verify(profileRepository).deleteById(999999L)
        }
    }
}

