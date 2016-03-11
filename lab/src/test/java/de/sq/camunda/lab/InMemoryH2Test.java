package de.sq.camunda.lab;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.consulting.process_test_coverage.ProcessTestCoverage;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class InMemoryH2Test {

  @Rule
  public ProcessEngineRule processEngineRule = new ProcessEngineRule();

  private static final String PROCESS_DEFINITION_KEY = "lab";

  static {
    LogFactory.useSlf4jLogging(); // MyBatis
  }

  @Before
  public void setup() {
    init(processEngineRule.getProcessEngine());
  }

  /**
   * Just tests if the process definition is deployable.
   */
  @Test
  @Deployment(resources = "process.bpmn")
  public void testParsingAndDeployment() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }

  @Test
  @Deployment(resources = "process.bpmn")
  public void testHappyPath() {
	  //process variables
	  Map<String, Object> variables = new HashMap<String, Object>();
	  variables.put("content", "SQ / Test not approved (please don't tweet this!)");
	  variables.put("approved", Boolean.FALSE);
	  
	  ProcessInstance processInstance = processEngine().getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);
	  
    Task task = processEngineRule.getTaskService().createTaskQuery().taskAssignee("demo").singleResult();
    processEngine().getTaskService().complete(task.getId(), variables);
	  
    // assert that all activities where passed
    assertThat(processInstance) //
    	.isEnded() //
    	.hasPassed("end_event_tweet_handled", "gateway_approved", "gateway_join", "start_event_new_tweet", "user_task_review_tweet");

    ProcessTestCoverage.calculate(processInstance, processEngineRule.getProcessEngine());
  }

  @After
  public void calculateCoverageForAllTests() throws Exception {
    ProcessTestCoverage.calculate(processEngineRule.getProcessEngine());
  }  

}
