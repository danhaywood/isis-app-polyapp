/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package domainapp.dom.modules.comms;

import domainapp.dom.modules.poly.PolymorphicLinkHelper;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CommunicationChannelsContributionsTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    DomainObjectContainer mockContainer;

    @Mock
    CommunicationChannelOwner mockCommunicationChannelOwner;

    @Mock
    PolymorphicLinkHelper<CommunicationChannel, CommunicationChannelOwner, CommunicationChannelOwnerLink, CommunicationChannelsContributions.CommunicationChannelOwnerLinkInstantiateEvent> mockHelper;

    CommunicationChannelsContributions communicationChannelsContributions;


    @Before
    public void setUp() throws Exception {
        communicationChannelsContributions = new CommunicationChannelsContributions();
        communicationChannelsContributions.container = mockContainer;

        communicationChannelsContributions.ownerLinkHelper = mockHelper;
    }

    public static class Create extends CommunicationChannelsContributionsTest {

        @Test
        public void happyCase() throws Exception {

            // given
            final CommunicationChannel communicationChannel = new CommunicationChannel();
            final CommunicationChannelOwnerLink ownerLink = new CommunicationChannelOwnerLink(){};

            final Sequence seq = context.sequence("create");
            context.checking(new Expectations() {
                {
                    oneOf(mockContainer).newTransientInstance(CommunicationChannel.class);
                    inSequence(seq);
                    will(returnValue(communicationChannel));

                    oneOf(mockContainer).persist(communicationChannel);
                    inSequence(seq);

                    oneOf(mockContainer).flush();
                    inSequence(seq);

                    oneOf(mockHelper).createLink(communicationChannel, mockCommunicationChannelOwner);
                    inSequence(seq);
                }
            });

            // when
            final CommunicationChannel obj = communicationChannelsContributions.createCommunicationChannel(mockCommunicationChannelOwner, "01-234-5678");

            // then
            assertThat(obj, is(communicationChannel));
            assertThat(obj.getDetails(), is("01-234-5678"));
        }

    }

}
