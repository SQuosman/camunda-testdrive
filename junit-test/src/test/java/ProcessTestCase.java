import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessTestCase {

	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule(
			new StandaloneInMemProcessEngineConfiguration().buildProcessEngine());

	ProcessEngine processEngine = rule.getProcessEngine();
//	 @Test
//	 @Deployment(resources = "junit-test-process.bpmn")
//	 public void testLessThanFivePath() {
//	 //processEngine = rule.getProcessEngine();
//	 ProcessInstance pi = processEngine.getRuntimeService()
//	 .startProcessInstanceByKey("junit-test");
//	
//	 assertThat(pi).task("DoSomethingUserTask").hasCandidateGroup("management");
//	
//	 // Liste der Management-Tasks bekommen
//	 List<Task> tasks = processEngine.getTaskService().createTaskQuery()
//	 .taskCandidateGroup("management").list();
//	 assertEquals(1, tasks.size());
//	
//	 // Prozessvariable erstellen. Container ist vom Typ Map, String enthält
//	 // Variablenname, der im Prozess zB für Entscheidungen benutzt wird
//	 Map<String, Object> variables = new HashMap<String, Object>();
//	 variables.put("a", 4);
//	 // task erfüllen und Prozessvariable zuweisen
//	 processEngine.getTaskService().complete(tasks.get(0).getId(), variables);
//	
//	 //Timer-Job erfragen und manuell ausführen
//	 Job job =
//	 processEngine.getManagementService().createJobQuery().timers().singleResult();
//	 processEngine.getManagementService().executeJob(job.getId());
//	
//	 assertThat(pi).isStarted().isEnded().hasPassed("LessThanFiveEndEvent");
//	 }

	@Test
	@Deployment(resources = "junit-test-process.bpmn")
	public void testGreaterThanFivePath() {
//		processEngine = rule.getProcessEngine();
		ProcessInstance pi = processEngine.getRuntimeService()
				.startProcessInstanceByKey("junit-test");

		assertThat(pi).task("DoSomethingUserTask").hasCandidateGroup("management");

		// Liste der Management-Tasks bekommen
		List<Task> tasks = processEngine.getTaskService().createTaskQuery()
				.taskCandidateGroup("management").list();
		assertEquals(1, tasks.size());

		// Prozessvariable erstellen. Container ist vom Typ Map, String enthält
		// Variablenname, der im Prozess zB für Entscheidungen benutzt wird
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("a", 6);
		// task erfüllen und Prozessvariable zuweisen
		processEngine.getTaskService().complete(tasks.get(0).getId(), variables);

		// Execution-Instanz mit Nachrichtenevent erfragen und Nachricht manuell
		// "senden"
		Execution execution = processEngine.getRuntimeService().createExecutionQuery()
				.processInstanceId(pi.getId())
				.messageEventSubscriptionName("Message_testnachricht").singleResult();
		processEngine.getRuntimeService().messageEventReceived("Message_testnachricht",
				execution.getId());

		assertThat(pi).isStarted().isEnded().hasPassed("GreaterThanFiveEndEvent");
	}
}
