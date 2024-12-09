package se.sundsvall.vacationdocument.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Map;
import net.javacrumbs.shedlock.core.DefaultLockManager;
import net.javacrumbs.shedlock.core.DefaultLockingTaskExecutor;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.spring.LockableTaskScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import se.sundsvall.vacationdocument.integration.opene.OpenEClientProperties;

@ExtendWith(MockitoExtension.class)
class VacationDocumentSchedulerTests {

	private static final String MUNICIPALITY_ID_1 = "1984";
	private static final String MUNICIPALITY_ID_2 = "1985";

	@Mock
	private OpenEClientProperties.OpenEEnvironment openEEnvironmentMock1;
	@Mock
	private OpenEClientProperties.OpenEEnvironment.Scheduling openEEnvironmentSchedulingMock1;
	@Mock
	private OpenEClientProperties.OpenEEnvironment openEEnvironmentMock2;
	@Mock
	private OpenEClientProperties.OpenEEnvironment.Scheduling openEEnvironmentSchedulingMock2;

	@Mock
	private OpenEClientProperties openEClientPropertiesMock;
	@Mock
	private VacationDocumentService vacationDocumentServiceMock;
	@Mock
	private TaskScheduler taskSchedulerMock;
	@Mock
	private LockProvider lockProviderMock;

	@Test
	void schedulerSetup() {
		when(openEEnvironmentSchedulingMock1.enabled()).thenReturn(false);
		when(openEEnvironmentMock1.scheduling()).thenReturn(openEEnvironmentSchedulingMock1);
		when(openEEnvironmentSchedulingMock2.enabled()).thenReturn(true);
		when(openEEnvironmentMock2.scheduling()).thenReturn(openEEnvironmentSchedulingMock2);
		when(openEClientPropertiesMock.environments()).thenReturn(
			Map.of(MUNICIPALITY_ID_1, openEEnvironmentMock1, MUNICIPALITY_ID_2, openEEnvironmentMock2));

		try (var mockDefaultLockingTaskExecutor = mockConstruction(DefaultLockingTaskExecutor.class);
			var mockLockConfiguration = mockConstruction(LockConfiguration.class);
			var mockDefaultLockManager = mockConstruction(DefaultLockManager.class);
			var mockCronTrigger = mockConstruction(CronTrigger.class);
			var mockLockableTaskScheduler = mockConstruction(LockableTaskScheduler.class)) {
			new VacationDocumentScheduler(openEClientPropertiesMock, vacationDocumentServiceMock, taskSchedulerMock, lockProviderMock);

			assertThat(mockDefaultLockingTaskExecutor.constructed()).hasSize(1);
			assertThat(mockLockConfiguration.constructed()).hasSize(1);
			assertThat(mockDefaultLockManager.constructed()).hasSize(1);
			assertThat(mockCronTrigger.constructed()).hasSize(1);
			assertThat(mockLockableTaskScheduler.constructed()).hasSize(1);
		}
	}

	@Nested
	class ProcessDocumentsTaskTests {

		@Mock
		private VacationDocumentService vacationDocumentServiceMock;

		private VacationDocumentScheduler.ProcessDocumentsTask processDocumentsTask;

		@BeforeEach
		void setUp() {
			processDocumentsTask = new VacationDocumentScheduler.ProcessDocumentsTask(vacationDocumentServiceMock, MUNICIPALITY_ID_1);
		}

		@Test
		void run() {
			var today = LocalDate.now();
			var yesterday = today.minusDays(1);

			processDocumentsTask.run();

			verify(vacationDocumentServiceMock).processDocuments(MUNICIPALITY_ID_1, yesterday, today);
			verifyNoMoreInteractions(vacationDocumentServiceMock);
		}
	}
}
