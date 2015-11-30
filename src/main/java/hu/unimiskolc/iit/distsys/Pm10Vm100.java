package hu.unimiskolc.iit.distsys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

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
		
		double minCPU = Double.MAX_VALUE;
		double minprocessing = Double.MAX_VALUE;
		long minmemory = Long.MAX_VALUE;
		
		Repository repo = iaas.repositories.get(0);
		Collection<StorageObject> sos = iaas.repositories.get(0).contents();
		StorageObject so = sos.iterator().next();
		VirtualAppliance va = (VirtualAppliance)so;
		ResourceConstraints allCaps = iaas.getCapacities();
		
		
		for(PhysicalMachine pm : iaas.machines){
			ResourceConstraints capacities = pm.getCapacities();
			
			maxCPU = Math.max(maxCPU, capacities.getRequiredCPUs());
			maxprocessing = Math.max(maxprocessing, capacities.getRequiredProcessingPower());
			maxmemory = Math.max(maxmemory, capacities.getRequiredMemory());
			
			minCPU = Math.min(minCPU, capacities.getRequiredCPUs());
			minprocessing = Math.min(minprocessing, capacities.getRequiredProcessingPower());
			minmemory = Math.min(minmemory, capacities.getRequiredMemory());
		}
		
		
		System.out.println("----------");
		
		//cc = new ConstantConstraints(allCaps.getRequiredCPUs()/vmCount, maxprocessing, maxmemory/vmCount);
		cc = new ConstantConstraints(allCaps.getRequiredCPUs()/vmCount, minprocessing, minmemory/vmCount);
			
		try{
			iaas.requestVM(va, cc, repo, vmCount - iaas.machines.size());
			Timed.simulateUntilLastEvent();
			
			ArrayList<PhysicalMachine> pms = new ArrayList<PhysicalMachine>(iaas.machines);
			
			Collections.sort(pms, new Comparator<PhysicalMachine>(){
				public int compare(PhysicalMachine pm1, PhysicalMachine pm2){
					Double diff = Math.signum(pm1.freeCapacities.getTotalProcessingPower()-pm2.freeCapacities.getTotalProcessingPower());
					return diff.intValue();
				}
			});
			
			
			for(PhysicalMachine pm : pms){
				ConstantConstraints cc2 = new ConstantConstraints(
						//Trick from teacher
						pm.freeCapacities.getRequiredCPUs()*
						pm.getCapacities().getRequiredProcessingPower()/
						pm.freeCapacities.getRequiredProcessingPower(),
						pm.freeCapacities.getRequiredProcessingPower(),
						pm.freeCapacities.getRequiredMemory());
		
				iaas.requestVM(va, cc2, repo, 1);
				Timed.simulateUntilLastEvent();
				
				System.out.println("Done.");
			}
		}catch(Exception e){
			System.out.println(e);
		}
	}
}
