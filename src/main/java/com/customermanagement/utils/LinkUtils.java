package com.customermanagement.utils;

import com.customermanagement.config.UriConfig;
import com.customermanagement.controller.CustomerController;
import com.customermanagement.domain.CustomerDetails;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LinkUtils {

    public static Link buildSelfHateoasLinks(UriConfig uriConfig, CustomerDetails c, Long customerId){
        Map<String, Object> queryParamMap = Map.of("id", customerId);

        List<TemplateVariable> templateVariablesList = queryParamMap.keySet().stream()
                .map(k -> new TemplateVariable(k, TemplateVariable.VariableType.REQUEST_PARAM)).collect(Collectors.toList());

        TemplateVariables templateVariables = new TemplateVariables(templateVariablesList);

        UriTemplate uriTemplate = UriTemplate.of(WebMvcLinkBuilder.linkTo(CustomerController.class)
        .slash(uriConfig.getCustomer()).toUriComponentsBuilder().build().toUriString(), templateVariables);

        return Link.of(uriTemplate, "self").expand(queryParamMap);


    }
}
