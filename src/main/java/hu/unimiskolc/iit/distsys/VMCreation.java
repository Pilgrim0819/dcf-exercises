package hu.unimiskolc.iit.distsys;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine.ResourceAllocation;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ConstantConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.io.StorageObject;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;
import hu.unimiskolc.iit.distsys.interfaces.VMCreationApproaches;

public class VMCreation implements VMCreationApproaches{
	@Override
	public void directVMCreation() throws Exception{
		PhysicalMachine pm = ExercisesBase.getNewPhysicalMachine();
		pm.turnon();
		
		Timed.simulateUntilLastEvent();
		
		VirtualAppliance va = new VirtualAppliance("2", 1, 0);
		pm.localDisk.registerObject(va);
		ConstantConstraints requested = new ConstantConstraints(1, 100, 4096);
		
		pm.requestVM(va, requested, pm.localDisk, 1);
		Timed.simulateUntilLastEvent();
		
		VirtualAppliance va2 = new VirtualAppliance("3", 1, 0);
		pm.localDisk.registerObject(va2);
		
		pm.requestVM(va2, requested, pm.localDisk, 1);
		Timed.simulateUntilLastEvent();
	}
	@Override
	public void twoPhaseVMCreation() throws Exception{
		PhysicalMachine pm = ExercisesBase.getNewPhysicalMachine();
		pm.turnon();
		
		Timed.simulateUntilLastEvent();
		
		VirtualAppliance va = new VirtualAppliance("2", 1, 0);
		pm.localDisk.registerObject(va);
		VirtualMachine vm = new VirtualMachine(va);
		ConstantConstraints requested = new ConstantConstraints(1, 100, 4096);
		ResourceAllocation ra = pm.allocateResources(requested, false, 1);
		
		pm.deployVM(vm, ra, pm.localDisk);
		Timed.simulateUntilLastEvent();
		
		VirtualAppliance va2 = new VirtualAppliance("3", 1, 0);
		pm.localDisk.registerObject(va2);
		VirtualMachine vm2 = new VirtualMachine(va2);
		ResourceAllocation ra2 = pm.allocateResources(requested, false, 1);
		
		pm.deployVM(vm2, ra2, pm.localDisk);
		Timed.simulateUntilLastEvent();
	}
	@Override
	public void indirectVMCreation() throws Exception{
		PhysicalMachine pm = ExercisesBase.getNewPhysicalMachine();
		
		IaaSService iaas = ExercisesBase.getNewIaaSService();
		iaas.registerHost(pm);
		
		VirtualAppliance va = new VirtualAppliance("1", 1, 0);
		pm.localDisk.registerObject(va);
		ConstantConstraints requested = new ConstantConstraints(1, 100, 4096);
		
		iaas.requestVM(va, requested, pm.localDisk, 1);
		Timed.simulateUntilLastEvent();
	}
	@Override
	public void migratedVMCreation() throws Exception{
		/*PhysicalMachine pm = ExercisesBase.getNewPhysicalMachine();
		pm.turnon();
		
		Timed.simulateUntilLastEvent();
		
		PhysicalMachine pm2 = ExercisesBase.getNewPhysicalMachine();
		pm2.turnon();
		
		Timed.simulateUntilLastEvent();
		
		VirtualAppliance va = new VirtualAppliance("2", 1, 0);
		pm2.localDisk.registerObject(va);
		ConstantConstraints requested = new ConstantConstraints(1, 100, 4096);
		
		pm2.requestVM(va, requested, pm.localDisk, 1);
		Timed.simulateUntilLastEvent();*/
		
		PhysicalMachine pmInitial = ExercisesBase.getNewPhysicalMachine();
		PhysicalMachine pmTarget = ExercisesBase.getNewPhysicalMachine();
		pmInitial.turnon();
		pmTarget.turnon();
		Timed.simulateUntilLastEvent();
		VirtualAppliance va = new VirtualAppliance("VAID", 10, 0, false,
				100000000l);
		pmInitial.localDisk.registerObject(va);
		ConstantConstraints migratingCaps = new ConstantConstraints(Math.min(
				pmInitial.getCapacities().getRequiredCPUs(), pmTarget
						.getCapacities().getRequiredCPUs()), Math.min(pmInitial
				.getCapacities().getRequiredProcessingPower(), pmTarget
				.getCapacities().getRequiredProcessingPower()), Math.min(
				pmInitial.getCapacities().getRequiredMemory(), pmTarget
						.getCapacities().getRequiredMemory()));
		VirtualMachine vm = pmInitial.requestVM(va, migratingCaps,
				pmInitial.localDisk, 1)[0];
		Timed.simulateUntilLastEvent();
		ResourceAllocation ra = pmTarget.allocateResources(migratingCaps, true,
				PhysicalMachine.migrationAllocLen * 1000);
		vm.migrate(ra);
		Timed.simulateUntilLastEvent();
	}

}
