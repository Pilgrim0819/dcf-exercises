package hu.unimiskolc.iit.distsys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.helpers.job.Job;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine.State;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine.StateChange;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ConstantConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption.ConsumptionEvent;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;
import hu.unimiskolc.iit.distsys.interfaces.BasicJobScheduler;

public class RRJSched implements BasicJobScheduler{
	IaaSService IaaS;
	VirtualMachine vm;
	int vmIndex = 0;
	int machinesNum = 0;
	Random rand = new Random();
	
	public void setupIaaS(IaaSService iaas){
		this.IaaS = iaas;
		this.machinesNum = iaas.machines.size();
	}
	
	public void handleJobRequestArrival(Job j){
		ComplexDCFJob job = (ComplexDCFJob)j;
		Repository r = this.IaaS.repositories.get(0);		
		VirtualAppliance va = new VirtualAppliance(Integer.toString(vmIndex), 1, 0);
		
		try{
			r.registerObject(va);
			this.vm = this.IaaS.requestVM(va, this.IaaS.machines.get(rand.nextInt(this.machinesNum)).getCapacities(), r, 1)[0];
			
			StateChange consumer = new VMStateChange(job);
			vm.subscribeStateChange(consumer);
			
			vmIndex++;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void setupVMset(Collection<VirtualMachine> vms) {
		// TODO Auto-generated method stub
		
	}
}

class VMStateChange implements StateChange{
	ComplexDCFJob j;
	
	public VMStateChange(ComplexDCFJob j){
		this.j = j;
	}

	@Override
	public void stateChanged(VirtualMachine vm, State oldState, State newState) {
		if(newState == State.RUNNING){
			try{
				ConsumptionEvent ce = new CustomConsumptionEvent();
				this.j.startNowOnVM(vm, ce);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
}
