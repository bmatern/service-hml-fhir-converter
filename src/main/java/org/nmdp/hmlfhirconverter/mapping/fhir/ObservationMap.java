package org.nmdp.hmlfhirconverter.mapping.fhir;


/**
 * Created by Andrew S. Brown, Ph.D., <abrown3@nmdp.org>, on 4/13/17.
 * <p>
 * service-hmlFhirConverter
 * Copyright (c) 2012-2017 National Marrow Donor Program (NMDP)
 * <p>
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 3 of the License, or (at
 * your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; with out even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library;  if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA.
 * <p>
 * > http://www.fsf.org/licensing/licenses/lgpl.html
 * > http://www.opensource.org/licenses/lgpl-license.php
 */

import io.swagger.model.Typing;
import org.modelmapper.Converter;

import io.swagger.model.Hml;

import org.modelmapper.spi.MappingContext;
import org.nmdp.hmlfhirconverter.domain.fhir.Code;
import org.nmdp.hmlfhirconverter.domain.fhir.Observation;

public class ObservationMap implements Converter<Hml, Observation>{

    @Override
    public Observation convert(MappingContext<Hml, Observation> context) {
        if (context.getSource() == null) {
            return null;
        }

        Hml hml = context.getSource();
        Typing typing = hml.getSamples().get(0).getTyping();
        Observation observation = new Observation();
        Code code = new Code();

        code.setName(typing.getGeneFamily());
        observation.setCode(code);

        return observation;
    }
}