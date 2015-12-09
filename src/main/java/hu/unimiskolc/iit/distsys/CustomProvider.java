package hu.unimiskolc.iit.distsys;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;

import hu.mta.sztaki.lpds.cloud.simulator.energy.specialized.IaaSEnergyMeter;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.unimiskolc.iit.distsys.forwarders.IaaSForwarder;
import hu.unimiskolc.iit.distsys.interfaces.CloudProvider;

public class CustomProvider implements CloudProvider, VMManager.CapacityChangeEvent<PhysicalMachine>{
	IaaSService ia;

	@Override
	public double getPerTickQuote(ResourceConstraints rc){
		int pmcount = ia.machines.size();
		double energyConsumption=0;
		int numOfVms=0;
		double total =0;
		double utilization=ia.getRunningCapacities().getTotalProcessingPower()/ia.getCapacities().getTotalProcessingPower();
		double basePrice = 1;
		
		for(PhysicalMachine pm : ia.machines){
			energyConsumption += pm.getPerTickProcessingPower();
			numOfVms += pm.numofCurrentVMs();
			total += rc.getRequiredCPUs()*basePrice*utilization*numOfVms;
		}
		
		return total;
	}
	
	@Override
	public void capacityChanged(ResourceConstraints newcap, List<PhysicalMachine> affected) {
		final boolean newreg = ia.isRegisteredHost(affected.get(0));
		
		if(!newreg){
			for(PhysicalMachine pm : affected){
				try{
					ia.registerHost(ExercisesBase.getNewPhysicalMachine(RandomUtils.nextDouble(2, 5)));
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void setIaaSService(IaaSService iaas) {
		ia = iaas;
		ia.subscribeToCapacityChanges(this);
		((IaaSForwarder)ia).setQuoteProvider(this);
	}
	
}
