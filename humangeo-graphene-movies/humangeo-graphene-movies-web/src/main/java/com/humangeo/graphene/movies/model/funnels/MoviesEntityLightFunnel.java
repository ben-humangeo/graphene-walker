package com.humangeo.graphene.movies.model.funnels;

import graphene.model.Funnel;
import graphene.model.idl.G_Entity;
import graphene.model.idl.G_Property;
import graphene.model.idl.G_PropertyTag;
import graphene.model.idlhelper.EntityHelper;
import graphene.model.idlhelper.PropertyHelper;
import graphene.model.view.entities.EntityAttribute;
import graphene.model.view.entities.EntityLight;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * Created by bparrish on 12/23/14.
 */
public class MoviesEntityLightFunnel implements Funnel<EntityLight, G_Entity> {
    @Override
    public G_Entity from(EntityLight e) {
        // TODO
        throw new NotImplementedException();
    }

    @Override
    public EntityLight to(G_Entity e) {
        EntityLight entityLight = new EntityLight();

        entityLight.setId(e.getUid());

        entityLight.setDatasource_id(e.getProvenance().getUri());

        StringBuffer sb = new StringBuffer("");

        List<G_Property> propertiesByTag = EntityHelper.getPropertiesByTag(e, G_PropertyTag.NAME);

        entityLight.setEffectiveName(e.getUid());

        for (G_Property p : propertiesByTag) {
            if (sb.length() > 0) {
                sb.append(", ");
            }

            // get the value of the property
            String n = (String) PropertyHelper.from(p).getValue();

            entityLight.getAttributes().add(new EntityAttribute("Name", "", n));

            sb.append(n);
        }

        if (propertiesByTag.size() > 1) {
            entityLight.setEffectiveName(entityLight.getEffectiveName() + " ("
                    + propertiesByTag.size() + " associated values)");
        }

        entityLight.setAllNames(sb.toString());

        for (G_Property p :  EntityHelper.getPropertiesByKey(e, "communicationId")) {
            entityLight.getAttributes().add(
                    new EntityAttribute("CommunicationId", p.getKey(),
                            (String) PropertyHelper.from(p).getValue()));
        }

        for (G_Property p : EntityHelper.getPropertiesByTag(e, G_PropertyTag.GEO)) {
            entityLight.getAttributes().add(
                    new EntityAttribute("Address", p.getKey(),
                            (String) PropertyHelper.from(p).getValue()));
        }

        for (G_Property p : EntityHelper.getPropertiesByKey(e, "email")) {
            entityLight.getAttributes().add(
                    new EntityAttribute("Email", p.getKey(),
                            (String) PropertyHelper.from(p).getValue()));
        }

        for (G_Property p : EntityHelper.getPropertiesByTag(e, G_PropertyTag.ID)) {
            entityLight.getAttributes().add(
                    new EntityAttribute("Identifier", (String) PropertyHelper
                            .from(p).getValue(), (String) PropertyHelper
                            .from(p).getValue()));
        }

        for (G_Property ac : EntityHelper.getPropertiesByKey(e, "account")) {
            entityLight.getAccountList()
                    .add((String) PropertyHelper.from(ac).getValue());
        }

        return entityLight;
    }
}
