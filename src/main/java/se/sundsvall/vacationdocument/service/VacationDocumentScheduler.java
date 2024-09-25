package se.sundsvall.vacationdocument.service;

import static java.time.Duration.ZERO;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import se.sundsvall.vacationdocument.integration.opene.OpenEClientProperties;

import net.javacrumbs.shedlock.core.DefaultLockManager;
import net.javacrumbs.shedlock.core.DefaultLockingTaskExecutor;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.spring.LockableTaskScheduler;

@Component
class VacationDocumentScheduler {

    VacationDocumentScheduler(final OpenEClientProperties openEProperties,
            final VacationDocumentService vacationDocumentService,
            final TaskScheduler taskScheduler,
            final LockProvider lockProvider) {
        var executor = new DefaultLockingTaskExecutor(lockProvider);

        openEProperties.environments().forEach((municipalityId, environment) -> {
            if (environment.scheduling().enabled()) {
                var lockConfiguration = new LockConfiguration(Instant.now(), "lock-" + municipalityId, environment.scheduling().lockAtMostFor(), ZERO);
                var lockManager = new DefaultLockManager(executor, lockConfigurationExtractor -> Optional.of(lockConfiguration));
                var cronTrigger = new CronTrigger(environment.scheduling().cronExpression());
                var lockableTaskScheduler = new LockableTaskScheduler(taskScheduler, lockManager);
                var task = new ProcessDocumentsTask(vacationDocumentService, municipalityId);

                lockableTaskScheduler.schedule(task, cronTrigger);
            }
        });
    }

    record ProcessDocumentsTask(VacationDocumentService vacationDocumentService, String municipalityId) implements Runnable {

        @Override
        public void run() {
            var today = LocalDate.now();
            var yesterday = today.minusDays(1);

            vacationDocumentService.processDocuments(municipalityId, yesterday, today);
        }
    }
}
