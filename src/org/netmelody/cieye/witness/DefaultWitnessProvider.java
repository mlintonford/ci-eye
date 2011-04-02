package org.netmelody.cieye.witness;

import java.util.HashMap;
import java.util.Map;

import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.core.observation.Detective;
import org.netmelody.cieye.persistence.State;
import org.netmelody.cieye.witness.demo.DemoModeWitness;
import org.netmelody.cieye.witness.jenkins.JenkinsWitness;
import org.netmelody.cieye.witness.teamcity.TeamCityWitness;

public final class DefaultWitnessProvider implements WitnessProvider {

    private final Map<String, CiSpy> witnesses = new HashMap<String, CiSpy>();
    private final Detective detective;
    
    public DefaultWitnessProvider(State state) {
        detective = state.detective();
    }
    
    @Override
    public CiSpy witnessFor(Feature feature) {
        if (witnesses.containsKey(feature.endpoint())) {
            return witnesses.get(feature.endpoint());
        }
        
        CiSpy witness = new DemoModeWitness(detective);
        if (CiServerType.JENKINS.equals(feature.type())) {
            witness = new BufferedWitness(new JenkinsWitness(feature.endpoint(), detective));
        }
        else if (CiServerType.TEAMCITY.equals(feature.type())) {
            witness = new BufferedWitness(new TeamCityWitness(feature.endpoint(), detective));
        }
        witnesses.put(feature.endpoint(), witness);
        return witness;
    }

}