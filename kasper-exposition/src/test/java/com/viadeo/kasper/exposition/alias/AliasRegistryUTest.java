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
package com.viadeo.kasper.exposition.alias;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class AliasRegistryUTest {

    @Test(expected = NullPointerException.class)
    public void register_withNullAsName_shouldThrowException() {
        // Given
        final AliasRegistry aliasRegistry = new AliasRegistry();

        // When
        aliasRegistry.register(null, Lists.<String>newArrayList());

        // Then throws an exception
    }

    @Test(expected = NullPointerException.class)
    public void register_withNullAsAliases_shouldThrowException() {
        // Given
        final AliasRegistry aliasRegistry = new AliasRegistry();

        // When
        aliasRegistry.register("toto", null);

        // Then throws an exception
    }

    @Test(expected = RuntimeException.class)
    public void register_withAlreadyRegisteredName_shouldThrowException() {
        // Given
        final String name = "toto";

        final AliasRegistry aliasRegistry = new AliasRegistry();
        aliasRegistry.register(name, Lists.<String>newArrayList());

        // When
        aliasRegistry.register(name, Lists.<String>newArrayList());

        // Then throws an exception
    }

    @Test(expected = RuntimeException.class)
    public void register_withAlreadyUsedAlias_shouldThrowException() {
        // Given
        final String alias = "?";

        final AliasRegistry aliasRegistry = new AliasRegistry();
        aliasRegistry.register("key1", Lists.<String>newArrayList("aliasA", "aliasB", alias));

        // When
        aliasRegistry.register("key1", Lists.<String>newArrayList("aliasC", "aliasD", alias));

        // Then throws an exception
    }

    @Test
    public void aliasesOf_withRegisteredName_shouldReturnAliases() {
        // Given
        final String name = "key";
        final List<String> aliases = Lists.newArrayList("aliasA", "aliasB");

        final AliasRegistry aliasRegistry = new AliasRegistry();
        aliasRegistry.register(name, aliases);

        // When
        final Optional<List<String>> optionalAliases = aliasRegistry.aliasesOf(name);

        // Then
        assertNotNull(optionalAliases);
        assertTrue(optionalAliases.isPresent());
        assertEquals(aliases, optionalAliases.get());
    }

    @Test
    public void aliasesOf_withUnregisteredName_shouldNothing() {
        // Given
        final String name = "key";

        final AliasRegistry aliasRegistry = new AliasRegistry();

        // When
        final Optional<List<String>> optionalAliases = aliasRegistry.aliasesOf(name);

        // Then
        assertNotNull(optionalAliases);
        assertFalse(optionalAliases.isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void resolve_withNull_shouldThrownException() {
        // Given
        final AliasRegistry aliasRegistry = new AliasRegistry();

        // When
        aliasRegistry.resolve(null);

        // Then throws an exception
    }

    @Test
    public void resolve_withUnknownAlias_shouldReturnValueGivenInParameter() {
        // Given
        final String input = "toto";
        final AliasRegistry aliasRegistry = new AliasRegistry();

        // When
        final String actualName = aliasRegistry.resolve(input);

        // Then
        assertNotNull(actualName);
        assertEquals(input, actualName);
    }

    @Test
    public void resolve_withRegisteredAlias_shouldReturnOriginalName() {
        // Given
        final String expectedName = "toto";
        final List<String> aliases = Lists.newArrayList("tutu", "tata");

        final AliasRegistry aliasRegistry = new AliasRegistry();
        aliasRegistry.register(expectedName, aliases);

        // When
        final String actualName = aliasRegistry.resolve(aliases.get(0));

        // Then
        assertNotNull(actualName);
        assertEquals(expectedName, actualName);
    }

}
