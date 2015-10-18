package hu.unimiskolc.iit.distsys;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption.ConsumptionEvent;

public class CustomConsumptionEvent implements ConsumptionEvent{

	@Override
	public void conComplete() {
		System.out.println("JOB finished!");
	}

	@Override
	public void conCancelled(ResourceConsumption problematic) {
		System.out.println("Cannot finish JOB!");
	}

}
