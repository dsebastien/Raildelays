package be.raildelays.batch.decider;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * This {@link JobExecutionDecider} is responsible to change the {@link org.springframework.batch.core.ExitStatus} of the <code>Step</code>
 * when we get an item with a delay greater than the max threshold in the
 * {@link org.springframework.batch.item.ExecutionContext}. Then we return <code>COMPLETED_WITH_60M_DELAY</code> in
 * order to go to extra steps to handle this particular item.
 *
 * @since 1.2
 * @author Almex
 * @see be.raildelays.batch.processor.StoreDelayGreaterThanThresholdInContextProcessor
 */
public class DelayMoreThanOneHourDecider implements JobExecutionDecider, InitializingBean {

    private String keyName;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.keyName, "The 'keyName' property must be provided");
    }

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        // The default status will be COMPLETED if the stepExecution is null
        FlowExecutionStatus finalStatus = FlowExecutionStatus.COMPLETED;
        ExecutionContext executionContext = stepExecution.getExecutionContext();

        if (stepExecution != null) {
            // We retrieve the effective status
            finalStatus = new FlowExecutionStatus(jobExecution.getExitStatus().toString());

            // Only if the previous step succeed we can go next
            if (stepExecution.getStatus().equals(BatchStatus.COMPLETED)) {
                // Only if the context contains what we expect we return our specific status
                if (executionContext.containsKey(keyName)) {
                    finalStatus = new FlowExecutionStatus("COMPLETED_WITH_60M_DELAY");
                }
            }
        }

        return finalStatus;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
}
