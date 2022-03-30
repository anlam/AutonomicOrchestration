package eu.arrowhead.autonomic.orchestrator.manager.execute;

import java.util.ArrayList;
import java.util.List;

import eu.arrowhead.autonomic.orchestrator.manager.knowledge.KnowledgeBase;
import eu.arrowhead.autonomic.orchestrator.manager.plan.Plan;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.AdaptationPlan;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.PlanStatus;
import eu.arrowhead.autonomic.orchestrator.manager.plan.model.SubstitutionAdaptation;
import eu.arrowhead.autonomic.orchestrator.mgmt.ArrowheadMgmtService;
import eu.arrowhead.autonomic.orchestrator.store.OrchestrationStoreRequestDTO;
import eu.arrowhead.autonomic.orchestrator.store.ServiceRegistryEntryListDTO;
import eu.arrowhead.common.dto.shared.ServiceRegistryResponseDTO;

public class OrchestrationPushWorker implements Runnable {

    private Thread t;
    protected Execute execute;
    private String name = "OrchestrationPushWorker";
    private String consumerName;
    private AdaptationPlan adaptationPlan;
    private Plan plan;
    private int consumerId;
    private ArrowheadMgmtService mgmtService;

    public OrchestrationPushWorker(ArrowheadMgmtService mgmtService, Execute ex, Plan plan, String name, int id,
            AdaptationPlan adaptPlan) {
        this.execute = ex;
        this.name = this.name + "_" + name;
        this.consumerName = name;
        this.consumerId = id;
        this.adaptationPlan = adaptPlan;
        this.plan = plan;
        this.mgmtService = mgmtService;
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, name);
            t.start();
        }
    }

    @Override
    public void run() {

        System.out.println("OrchestrationPushWorker Sending orchestratin to: " + consumerName);

        plan.UpdateAdaptationPlanStatus(consumerName, PlanStatus.SENDING);
        SubstitutionAdaptation aPlan = (SubstitutionAdaptation) adaptationPlan.getAdaptations().get(0);
        ServiceRegistryEntryListDTO producer_info_response = mgmtService
                .GetInforFromServiceDefinition(aPlan.getToService());
        if (producer_info_response != null) {
            String entryId = KnowledgeBase.getInstance().DeleteOrchestrationStoreEntry(consumerName,
                    aPlan.getFromService());
            if (entryId != null) {
                ServiceRegistryResponseDTO producer = producer_info_response.getData().get(0);
                OrchestrationStoreRequestDTO request = new OrchestrationStoreRequestDTO();
                request.setServiceDefinitionName(aPlan.getToService());
                request.setConsumerSystemId(consumerId);
                request.setProviderSystem(producer.getProvider());
                request.setServiceInterfaceName(producer.getInterfaces().get(0).getInterfaceName());
                request.setPriority("1");
                List<OrchestrationStoreRequestDTO> entries = new ArrayList<OrchestrationStoreRequestDTO>();
                entries.add(request);
                boolean addResponse = mgmtService.addOrchestrationStoreEntry(request);
                if (addResponse) {

                    boolean deleteResponse = mgmtService.deleteOrchestrationStoreEntry(entryId);
                    if (deleteResponse) {
                        plan.UpdateAdaptationPlanStatus(consumerName, PlanStatus.EXECUTED);
                        return;
                    }
                }
            }

        }
        plan.UpdateAdaptationPlanStatus(consumerName, PlanStatus.FAILED);

        // if (response != null) {
        // plan.UpdateAdaptationPlanStatus(consumerName, PlanStatus.SENT);
        // System.out.println("OrchestrationPushWorker get response: " + response);
        // /*
        // * List<Adaptation> executedAdapts = new ArrayList<Adaptation>(); for
        // * (Adaptation adapt : response.getAdaptations()) {
        // *
        // * if (adapt.getStatus() == PlanStatus.EXECUTED) {
        // *
        // * executedAdapts.add(adapt); }
        // *
        // * }
        // *
        // * for (Adaptation adapt :
        // * plan.GetAdaptationPlan(consumerName).getAdaptations()) {
        // * adapt.setStatus(PlanStatus.SENT); if (executedAdapts.contains(adapt))
        // * adapt.setStatus(PlanStatus.EXECUTED); }
        // */
        //
        // execute.ProcessExecutedAdaptationPlan(consumerName, response);
        // } else {
        // plan.UpdateAdaptationPlanStatus(consumerName, PlanStatus.FAILED);
        // System.err.println(String.format("Fail to send to %s, %s", consumerName, adaptationPlan));
        // }
    }

}
