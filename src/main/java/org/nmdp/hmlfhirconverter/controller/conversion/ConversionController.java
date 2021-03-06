package org.nmdp.hmlfhirconverter.controller.conversion;

/**
 * Created by Andrew S. Brown, Ph.D., <andrew@nmdp.org>, on 5/22/17.
 * <p>
 * service-hml-fhir-converter
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

import io.swagger.api.NotFoundException;
import org.nmdp.hmlfhirconvertermodels.dto.Hml;

import org.nmdp.hmlfhirconverter.service.HmlService;
import org.nmdp.hmlfhirconverter.service.conversion.HmlToFhirConversionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.Callable;
import java.util.List;

import org.apache.log4j.Logger;

@RestController
@RequestMapping("/conversion")
@CrossOrigin
public class ConversionController {
    private static final Logger LOG = Logger.getLogger(ConversionController.class);
    private final HmlToFhirConversionService hmlToFhirConversionService;
    private final HmlService hmlService;

    @Autowired
    public ConversionController(HmlToFhirConversionService hmlToFhirConversionService, HmlService hmlService) {
        this.hmlToFhirConversionService = hmlToFhirConversionService;
        this.hmlService = hmlService;
    }

    @RequestMapping(path = "/hmlToFhir/{xml}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public Callable<ResponseEntity<Boolean>> convertHmlToFhir(@PathVariable String xml) throws NotFoundException {
        try {
            hmlToFhirConversionService.convertHmlToFhir(xml);
            return () -> new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error("Error in singleton conversion hml to fhir.", ex);
            return () -> new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path = "/hmlToFhir", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public Callable<ResponseEntity<Boolean>> convertHmlFileToFhir(@RequestBody MultipartFile file) {
        try {
            List<Hml> hmls = hmlToFhirConversionService.convertFileToHml(file);
            //List<org.nmdp.hmlfhirconvertermodels.domain.Hml> nmdpHmls = hmlService.createItems(hmls);
            hmlToFhirConversionService.produceKafkaMessages(hmls);
            return () -> new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error("Error in file upload hml to fhir conversion.", ex);
            return () -> new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
