/*******************************************************************************
 * Welcome to the pedestrian simulation framework MomenTUM. 
 * This file belongs to the MomenTUM version 2.0.2.
 * 
 * This software was developed under the lead of Dr. Peter M. Kielar at the
 * Chair of Computational Modeling and Simulation at the Technical University Munich.
 * 
 * All rights reserved. Copyright (C) 2017.
 * 
 * Contact: peter.kielar@tum.de, https://www.cms.bgu.tum.de/en/
 * 
 * Permission is hereby granted, free of charge, to use and/or copy this software
 * for non-commercial research and education purposes if the authors of this
 * software and their research papers are properly cited.
 * For citation information visit:
 * https://www.cms.bgu.tum.de/en/31-forschung/projekte/456-momentum
 * 
 * However, further rights are not granted.
 * If you need another license or specific rights, contact us!
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

package tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.antOptimizationNavigation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.KneidlConstant;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.MacroRoutingAlgorithm;
import tum.cms.sim.momentum.model.tactical.routing.kneidlModel.navigation.weightTypes.IHandleTargetWeightsForGraph;
import tum.cms.sim.momentum.utility.graph.Edge;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Vertex;

public class MacroAntOptimizationColony 
	extends MacroRoutingAlgorithm 
	implements IHandleTargetWeightsForGraph {
	
	private HashSet<Vertex> forTargetInitialized = new HashSet<Vertex>();
	
	@Override
	public void initializeWeightsForTarget(Graph graph, Vertex target) {

		if(forTargetInitialized.contains(target)) {
			
			return;
		}
	
		String targetWeightName = target.getId().toString();
		
		for(Vertex current : graph.getVertices()) {
	
			for(Vertex successor : graph.getSuccessorVertices(current)) {
				
				graph.getEdge(current, successor)
					  .setWeight(KneidlConstant.AntOptColonyEdgePheromoneWeightNameSeed + targetWeightName,
							  	 KneidlConstant.PheromoneStartWeight);	
			}
		}
		
		this.forTargetInitialized.add(target);
	}
	
	public void decayPheromone(Graph graph,
			int numberOfAnts,
			HashMap<Vertex, Collection<IRichPedestrian>> pedestriansForTarget,
			SimulationState simulationState) {
		
		double totalPheromoneForGraph = graph.getEdgeCount();
		double decayOfCalls = KneidlConstant.PheromoneDecayTime / simulationState.getTimeStepDuration();
		double currentPheromoneDecay = 1.0 - (numberOfAnts / totalPheromoneForGraph) / decayOfCalls;

		for(Entry<Vertex, Collection<IRichPedestrian>> pedestrianForTarget : pedestriansForTarget.entrySet()) {
			
			if(pedestrianForTarget.getValue().size() > 0) {
				
				String targetWeightName = pedestrianForTarget.getKey().getId().toString();
				
				for(Vertex current : graph.getVertices()) {
					
					for(Edge edge : graph.getSuccessorEdges(current)) {
						
						edge.multiplicateWeight(KneidlConstant.AntOptColonyEdgePheromoneWeightNameSeed + targetWeightName, currentPheromoneDecay);
					}
		    	}
			}
		}
	}
}
