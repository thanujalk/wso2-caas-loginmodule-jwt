/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.security.caas.module.jwt.internal;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.security.caas.boot.ProxyLoginModule;
import org.wso2.carbon.security.caas.jaas.CarbonCallbackHandler;
import org.wso2.carbon.security.caas.jaas.util.CarbonSecurityConstants;
import org.wso2.carbon.security.caas.module.jwt.JWTLoginModule;
import org.wso2.carbon.security.caas.module.jwt.internal.osgi.JWTCallbackHandlerFactory;
import org.wso2.carbon.security.caas.module.jwt.internal.osgi.JWTLoginModuleFactory;
import org.wso2.carbon.security.caas.user.core.service.RealmService;

import java.util.Hashtable;
import java.util.Map;
import javax.security.auth.spi.LoginModule;

/**
 * OSGi component for JWT Login Module.
 *
 * @since 1.0.0
 */
@Component(
        name = "org.wso2.carbon.security.caas.module.jwt.JWTLoginModuleComponent",
        immediate = true
)
public class JWTLoginModuleComponent {

    private static final Logger log = LoggerFactory.getLogger(JWTLoginModuleComponent.class);

    /**
     * Register JWT LoginModule and Callback Handler as service factories.
     *
     * @param bundleContext Bundle Context.
     */
    @Activate
    public void registerCarbonSecurityConnectors(BundleContext bundleContext) {

        Hashtable<String, String> jwtLoginModuleProps = new Hashtable<>();
        jwtLoginModuleProps.put(ProxyLoginModule.LOGIN_MODULE_SEARCH_KEY, JWTLoginModule.class.getName());
        bundleContext.registerService(LoginModule.class, new JWTLoginModuleFactory(), jwtLoginModuleProps);

        Hashtable<String, String> jwtCallbackHandlerProps = new Hashtable<>();
        jwtCallbackHandlerProps.put(CarbonCallbackHandler.SUPPORTED_LOGIN_MODULE,
                                    CarbonSecurityConstants.JWT_LOGIN_MODULE);
        bundleContext.registerService(CarbonCallbackHandler.class, new JWTCallbackHandlerFactory(),
                                      jwtCallbackHandlerProps);
    }

    @Reference(
            name = "RealmService",
            service = RealmService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unregisterRealmService"
    )
    protected void registerRealmService(RealmService realmService, Map<String, String> properties) {
        JWTLoginModuleDataHolder.getInstance().registerCarbonRealmService(realmService);
    }

    protected void unregisterRealmService(RealmService realmService) {
        JWTLoginModuleDataHolder.getInstance().unregisterCarbonRealmService();
    }
}
