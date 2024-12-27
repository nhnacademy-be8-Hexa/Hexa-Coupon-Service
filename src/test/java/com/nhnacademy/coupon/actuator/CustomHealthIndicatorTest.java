package com.nhnacademy.coupon.actuator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CustomHealthIndicatorTest {

    @Test
    void testHealthWhenApplicationStatusIsDown() {
        // Arrange
        ApplicationStatus applicationStatusMock = mock(ApplicationStatus.class);
        when(applicationStatusMock.getStatus()).thenReturn(false);

        CustomHealthIndicator healthIndicator = new CustomHealthIndicator(applicationStatusMock);

        // Act
        Health health = healthIndicator.health();

        // Assert
        assertThat(health.getStatus().getCode()).isEqualTo("DOWN");
        verify(applicationStatusMock, times(1)).getStatus();
    }

    @Test
    void testHealthWhenApplicationStatusIsUp() {
        // Arrange
        ApplicationStatus applicationStatusMock = mock(ApplicationStatus.class);
        when(applicationStatusMock.getStatus()).thenReturn(true);

        CustomHealthIndicator healthIndicator = new CustomHealthIndicator(applicationStatusMock);

        // Act
        Health health = healthIndicator.health();

        // Assert
        assertThat(health.getStatus().getCode()).isEqualTo("UP");
        assertThat(health.getDetails()).containsEntry("service", "start");
        verify(applicationStatusMock, times(1)).getStatus();
    }
}
