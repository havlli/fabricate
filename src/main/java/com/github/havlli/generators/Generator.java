package com.github.havlli.generators;

import com.github.havlli.model.Address;
import com.github.havlli.model.Person;

public interface Generator {
    Person generatePerson();
    Address generateAddress();
}
