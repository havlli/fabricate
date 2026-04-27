package org.fabricate.generators;

import org.fabricate.model.Address;
import org.fabricate.model.Person;

public interface Generator {
    Person generatePerson();
    Address generateAddress();
}
