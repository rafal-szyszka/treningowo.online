package com.prodactivv.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

//    @Bean
//    CommandLineRunner runner(
//            PrimitiveRepository primitiveRepository,
//            TypeRepository typeRepository,
//            AttributeRepository attributeRepository,
//            TypeInstanceRepository typeInstanceRepository,
//            AttributeInstanceRepository attributeInstanceRepository,
//            PrimitiveInstanceRepository primitiveInstanceRepository
//    ) {
//        return args -> {
//
//            Type address = new Type("Address");
//            address = typeRepository.save(address);
//
//            Attribute street = new Attribute("Street");
//            street.setParentType(address);
//            street.setPrimitive(primitiveRepository.findById(1L).get());
//            attributeRepository.save(street);
//
//            Attribute buildingNo = new Attribute("BuildingNo");
//            buildingNo.setParentType(address);
//            buildingNo.setPrimitive(primitiveRepository.findById(1L).get());
//            attributeRepository.save(buildingNo);
//
//            Attribute premisesNo = new Attribute("PremisesNo");
//            premisesNo.setParentType(address);
//            premisesNo.setPrimitive(primitiveRepository.findById(1L).get());
//            attributeRepository.save(premisesNo);
//
//            Attribute city = new Attribute("City");
//            city.setParentType(address);
//            city.setPrimitive(primitiveRepository.findById(1L).get());
//            attributeRepository.save(city);
//
//            Attribute postalCode = new Attribute("PostalCode");
//            postalCode.setParentType(address);
//            postalCode.setPrimitive(primitiveRepository.findById(1L).get());
//            attributeRepository.save(postalCode);
//
//
//            Type user = new Type("User");
//            user = typeRepository.save(user);
//
//            Attribute name = new Attribute("Name");
//            name.setParentType(user);
//            name.setPrimitive(primitiveRepository.findById(1L).get());
//            attributeRepository.save(name);
//
//            Attribute lastName = new Attribute("LastName");
//            lastName.setParentType(user);
//            lastName.setPrimitive(primitiveRepository.findById(1L).get());
//            attributeRepository.save(lastName);
//
//            Attribute age = new Attribute("Age");
//            age.setParentType(user);
//            age.setPrimitive(primitiveRepository.findById(2L).get());
//            attributeRepository.save(age);
//
//            Attribute email = new Attribute("Email");
//            email.setParentType(user);
//            email.setPrimitive(primitiveRepository.findById(1L).get());
//            attributeRepository.save(email);
//
//            Attribute uAddress = new Attribute("Address");
//            uAddress.setParentType(user);
//            uAddress.setReferenceType(address);
//            attributeRepository.save(uAddress);
//
//            ObjectMapper mapper = new ObjectMapper();
////            System.out.println(mapper.writeValueAsString(typeRepository.findAll()));
//
//        };
//    }

}
