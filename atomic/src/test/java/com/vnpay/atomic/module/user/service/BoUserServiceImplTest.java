package com.vnpay.atomic.module.user.service;

import com.vnpay.atomic.module.user.dto.BoUserCreateRequest;
import com.vnpay.atomic.module.user.dto.BoUserResponse;
import com.vnpay.atomic.module.user.dto.BoUserUpdateRequest;
import com.vnpay.atomic.module.user.entity.BoUser;
import com.vnpay.atomic.module.user.entity.BoUserDocument;
import com.vnpay.atomic.module.user.repository.BoUserRepository;
import com.vnpay.atomic.module.user.repository.BoUserSearchRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoUserServiceImplTest {

    @Mock
    private BoUserRepository boUserRepository;

    @Mock
    private BoUserSearchRepository boUserSearchRepository;

    @InjectMocks
    private BoUserServiceImpl boUserService;

    private static BoUser buildSampleUser() {
        return BoUser.builder()
                .username("john.doe")
                .fullName("John Doe")
                .roleId(1L)
                .email("john@vnpay.vn")
                .mobileNo("0901234567")
                .branchCode("HCM01")
                .posCode("POS001")
                .status("A")
                .createdDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {

        @Test
        @DisplayName("should create user and sync to Elasticsearch")
        void create_success() {
            var request = new BoUserCreateRequest(
                    "john.doe", "John Doe", 1L, "john@vnpay.vn",
                    "0901234567", "HCM01", "POS001", "A"
            );
            BoUser saved = buildSampleUser();
            when(boUserRepository.save(any(BoUser.class))).thenReturn(saved);
            when(boUserSearchRepository.save(any(BoUserDocument.class)))
                    .thenReturn(BoUserDocument.from(saved));

            BoUserResponse result = boUserService.create(request);

            assertThat(result.username()).isEqualTo("john.doe");
            assertThat(result.fullName()).isEqualTo("John Doe");
            assertThat(result.email()).isEqualTo("john@vnpay.vn");
            verify(boUserRepository).save(any(BoUser.class));
            verify(boUserSearchRepository).save(any(BoUserDocument.class));
        }
    }

    @Nested
    @DisplayName("getByUsername()")
    class GetByUsernameTests {

        @Test
        @DisplayName("should return user when found")
        void getByUsername_success() {
            BoUser user = buildSampleUser();
            when(boUserRepository.findById("john.doe")).thenReturn(Optional.of(user));

            BoUserResponse result = boUserService.getByUsername("john.doe");

            assertThat(result.username()).isEqualTo("john.doe");
            assertThat(result.email()).isEqualTo("john@vnpay.vn");
            verify(boUserRepository).findById("john.doe");
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when user not found")
        void getByUsername_notFound() {
            when(boUserRepository.findById("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> boUserService.getByUsername("unknown"))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("unknown");
        }
    }

    @Nested
    @DisplayName("getAll()")
    class GetAllTests {

        @Test
        @DisplayName("should return list of all users")
        void getAll_success() {
            BoUser user = buildSampleUser();
            when(boUserRepository.findAll()).thenReturn(List.of(user));

            List<BoUserResponse> result = boUserService.getAll();

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().username()).isEqualTo("john.doe");
        }

        @Test
        @DisplayName("should return empty list when no users exist")
        void getAll_empty() {
            when(boUserRepository.findAll()).thenReturn(List.of());

            List<BoUserResponse> result = boUserService.getAll();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {

        @Test
        @DisplayName("should update user and sync to Elasticsearch")
        void update_success() {
            BoUser existing = buildSampleUser();
            when(boUserRepository.findById("john.doe")).thenReturn(Optional.of(existing));
            when(boUserRepository.save(any(BoUser.class))).thenReturn(existing);
            when(boUserSearchRepository.save(any(BoUserDocument.class)))
                    .thenReturn(BoUserDocument.from(existing));

            var request = new BoUserUpdateRequest(
                    "John Updated", 2L, "john.updated@vnpay.vn",
                    "0909876543", "HN01", "POS002", "I"
            );

            BoUserResponse result = boUserService.update("john.doe", request);

            assertThat(result).isNotNull();
            verify(boUserRepository).findById("john.doe");
            verify(boUserRepository).save(any(BoUser.class));
            verify(boUserSearchRepository).save(any(BoUserDocument.class));
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when updating non-existent user")
        void update_notFound() {
            when(boUserRepository.findById("unknown")).thenReturn(Optional.empty());

            var request = new BoUserUpdateRequest(
                    "Name", 1L, "e@e.com", null, null, null, "A"
            );

            assertThatThrownBy(() -> boUserService.update("unknown", request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("unknown");

            verify(boUserRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete()")
    class DeleteTests {

        @Test
        @DisplayName("should delete user from DB and Elasticsearch")
        void delete_success() {
            BoUser user = buildSampleUser();
            when(boUserRepository.findById("john.doe")).thenReturn(Optional.of(user));

            boUserService.delete("john.doe");

            verify(boUserRepository).delete(user);
            verify(boUserSearchRepository).deleteById("john.doe");
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when deleting non-existent user")
        void delete_notFound() {
            when(boUserRepository.findById("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> boUserService.delete("unknown"))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("unknown");

            verify(boUserRepository, never()).delete(any());
        }
    }
}
