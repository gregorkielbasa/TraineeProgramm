package org.lager.repository.xml;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lager.model.Basket;

import java.util.List;

import static org.lager.BasketFixtures.*;

@DisplayName("Basket XML Mapper")
class BasketXmlMapperTest implements WithAssertions {

    BasketXmlMapper xmlMapper = new BasketXmlMapper();

    @Nested
    @DisplayName("reads XML Record")
    class BasketXmlRead {

        @Test
        @DisplayName("null basket list")
        void nullBasketsList() {
            XmlBasketsList input = null;
            List<Basket> output = xmlMapper.xmlToBasketsList(input);

            assertThat(output).isEmpty();
        }

        @Test
        @DisplayName("empty basket list")
        void emptyBasketsList() {
            XmlBasketsList input = new XmlBasketsList(List.of());
            List<Basket> output = xmlMapper.xmlToBasketsList(input);

            assertThat(output).isEmpty();
        }

        @Test
        @DisplayName("basket with null number")
        void nullNumberBasket() {
            XmlBasketsList input = new XmlBasketsList(List.of(new XmlBasket(null, List.of())));
            List<Basket> output = xmlMapper.xmlToBasketsList(input);

            assertThat(output).isEmpty();
        }

        @Test
        @DisplayName("basket with null items list")
        void nullItemListBasket() {
            XmlBasketsList input = new XmlBasketsList(List.of(new XmlBasket(100_000_000L, null)));
            List<Basket> output = xmlMapper.xmlToBasketsList(input);

            assertThat(output).isEmpty();
        }

        @Test
        @DisplayName("basket with empty items list")
        void emptyItemList() {
            XmlBasketsList input = new XmlBasketsList(List.of(new XmlBasket(100_000_000L, List.of())));
            List<Basket> output = xmlMapper.xmlToBasketsList(input);

            assertThat(output).isEmpty();
        }

        @Test
        @DisplayName("simple basket list")
        void oneElement() {
            XmlBasketsList input = new XmlBasketsList(List.of(defaultXmlBasket()));
            List<Basket> output = xmlMapper.xmlToBasketsList(input);

            assertThat(output).containsExactly(defaultBasket());
        }

        @Test
        @DisplayName("item with null number")
        void emptyBasket() {
            List<XmlBasketItem> items = List.of(new XmlBasketItem(null, 1));
            XmlBasket basket = new XmlBasket(100_000_000L, items);

            XmlBasketsList input = new XmlBasketsList(List.of(basket));
            List<Basket> output = xmlMapper.xmlToBasketsList(input);

            assertThat(output).containsExactly(defaultEmptyBasket());
        }

        @Test
        @DisplayName("complex basket list")
        void bigBasketList() {
            XmlBasketsList input = new XmlBasketsList(List.of(defaultXmlEmptyBasket(), defaultXmlBasket(), anotherXmlBasket()));
            List<Basket> output = xmlMapper.xmlToBasketsList(input);

            assertThat(output).containsExactly(defaultBasket(), anotherBasket());
        }

        @Nested
        @DisplayName("writes XML Record")
        class BasketXmlWrite {

            @Test
            @DisplayName("null basket list")
            void nullBasketsList() {
                List<Basket> input = null;
                XmlBasketsList output = xmlMapper.basketsListToXml(input);

                assertThat(output.baskets()).isEmpty();
            }

            @Test
            @DisplayName("empty basket list")
            void emptyBasketsList() {
                List<Basket> input = List.of();
                XmlBasketsList output = xmlMapper.basketsListToXml(input);

                assertThat(output.baskets()).isEmpty();
            }

            @Test
            @DisplayName("empty basket item list")
            void emptyBasketItemList() {
                List<Basket> input = List.of(defaultEmptyBasket());
                XmlBasketsList output = xmlMapper.basketsListToXml(input);

                assertThat(output.baskets()).isEmpty();
            }

            @Test
            @DisplayName("simple basket list")
            void oneElement() {
                List<Basket> input = List.of(defaultBasket());
                XmlBasketsList output = xmlMapper.basketsListToXml(input);

                assertThat(output.baskets()).containsExactly(defaultXmlBasket());
            }

            @Test
            @DisplayName("complex basket list")
            void bigBasketList() {
                List<Basket> input = List.of(defaultBasket(), defaultEmptyBasket(), anotherBasket());
                XmlBasketsList output = xmlMapper.basketsListToXml(input);

                assertThat(output.baskets()).containsExactlyInAnyOrder(defaultXmlBasket(), anotherXmlBasket());
            }
        }
    }
}