package hu.unimiskolc.iit.distsys;

import java.util.Collection;

import org.junit.Assert;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine.State;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ConstantConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.io.StorageObject;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;
import hu.unimiskolc.iit.distsys.interfaces.FillInAllPMs;

public class Pm10Vm100 implements FillInAllPMs{
	
	ConstantConstraints cc;
	ResourceConstraints rc;
	VirtualMachine[] vms;

	@Override
	public void filler(IaaSService iaas, int vmCount){
		double maxCPU = 0.0;
		double maxprocessing = 0.0;
		long maxmemory = 0;
		Repository repo = iaas.repositories.get(0);
		Collection<StorageObject> sos = iaas.repositories.get(0).contents();
		StorageObject so = sos.iterator().next();
		VirtualAppliance va = (VirtualAppliance)so;
		
		for(PhysicalMachine pm : iaas.machines){
			System.out.println(pm.freeCapacities.getRequiredCPUs());
		}
		
		
		System.out.println("----------");
		
		for(int i=0;i<vmCount;i++){
			maxCPU = 0.0;
			for(PhysicalMachine pm : iaas.machines){
				if(pm.freeCapacities.getRequiredCPUs()>maxCPU){
					maxCPU = pm.freeCapacities.getRequiredCPUs();
					maxmemory = pm.freeCapacities.getRequiredMemory();
					maxprocessing = pm.freeCapacities.getRequiredProcessingPower();
				}
			}
			
			System.out.println(maxCPU);
			
			if(i<vmCount-iaas.machines.size()){
				cc = new ConstantConstraints(maxCPU/10.0, maxprocessing, maxmemory/10);
			}else{
				cc = new ConstantConstraints(maxCPU, maxprocessing, maxmemory);
			}
			
			try{
				vms = iaas.requestVM(va, cc, repo, 1);
				System.out.println(vms[0].getState());
				Timed.simulateUntilLastEvent();
				System.out.println(vms[0].getState());
			}catch(Exception e){
				System.out.println(e);
			}
		}
	}
}
