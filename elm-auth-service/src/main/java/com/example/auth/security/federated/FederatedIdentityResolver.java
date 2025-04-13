package com.example.auth.security.federated;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FederatedIdentityResolver {

    private final Map<String, FederatedIdentityProvider> providers;

    
    
    
//    Spring finds all @Component classes that implement FederatedIdentityProvider    + Collects them into a List<FederatedIdentityProvider>
//    Spring sees that  constructor wants a List<FederatedIdentityProvider> — so it automatically injects all matching beans.
//     at Runtime:
//    implementations = List.of(
//    	    new GoogleIdentityProvider(),     // getProviderName() = \"google\"
//    	    new MicrosoftIdentityProvider()  // getProviderName() = \"microsoft\"
//    	)
//    convert it to:
//    	providers = Map.of(
//    		    "google" -> GoogleIdentityProvider instance,
//    		    "microsoft" -> MicrosoftIdentityProvider instance
//    		)



    @Autowired
    public FederatedIdentityResolver(List<FederatedIdentityProvider> implementations) {
        this.providers = implementations.stream()
            .collect(Collectors.toMap(FederatedIdentityProvider::getProviderName, p -> p));
    }

    public FederatedIdentityProvider get(String providerName) {
        return providers.get(providerName.toLowerCase());
    }
}
