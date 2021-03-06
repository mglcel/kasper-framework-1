// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor.cache;

import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.core.component.command.interceptor.CommandValidationInterceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import junit.framework.TestCase;
import org.axonframework.commandhandling.interceptors.JSR303ViolationException;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Validation;
import javax.validation.constraints.Min;
import java.util.Locale;

public class BaseValidationInterceptorUTest extends TestCase {

    private class CommandToValidate implements Command {
        @Min(10)
        private int size;

        @NotEmpty
        private String company;

        public CommandToValidate(final int size, final String company) {
            this.size = size;
            this.company = company;
        }
    }


    public void testBuildExceptionMessage_whenThereAreSeveralConstraintsViolations_shouldListThemInTheExceptionMessage() throws Exception {
        // Given
        Locale.setDefault(Locale.US);
        final CommandValidationInterceptor<CommandToValidate> actor = new CommandValidationInterceptor<>(Validation.buildDefaultValidatorFactory());

        // When
        try {
            actor.process(
                new CommandToValidate(0, ""),
                Contexts.empty(),
                InterceptorChain.<CommandToValidate, CommandResponse>tail()
            );
            fail();
        } catch (final JSR303ViolationException e) {
            // Then
            assertEquals(true, e.getMessage().contains("One or more JSR303 constraints were violated."));
            assertEquals(true, e.getMessage().contains("Field company = []: may not be empty"));
            assertEquals(true, e.getMessage().contains("Field size = [0]: must be greater than or equal to 10"));
        }
    }

    public void testBuildExceptionMessage_whenThereIsOnlyOneConstraintViolation_shouldListItExceptionMessage() throws Exception {
        // Given
        Locale.setDefault(Locale.US);
        final CommandValidationInterceptor<CommandToValidate> actor = new CommandValidationInterceptor<>(Validation.buildDefaultValidatorFactory());

        // When
        try {
            actor.process(
                new CommandToValidate(0, "companyName"),
                Contexts.empty(),
                InterceptorChain.<CommandToValidate, CommandResponse>tail()
            );
            fail();
        } catch (final JSR303ViolationException e) {
            // Then
            assertEquals(true, e.getMessage().contains("One or more JSR303 constraints were violated."));
            assertEquals(true, e.getMessage().contains("Field size = [0]: must be greater than or equal to 10"));
        }
    }

}
