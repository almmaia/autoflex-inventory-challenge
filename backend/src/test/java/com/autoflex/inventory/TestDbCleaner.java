package com.autoflex.inventory;

import jakarta.enterprise.inject.spi.CDI;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

public class TestDbCleaner {
  public static void clean() {
    DataSource ds = CDI.current().select(DataSource.class).get();
    try (Connection c = ds.getConnection(); Statement st = c.createStatement()) {
      st.execute("DELETE FROM product_material");
      st.execute("DELETE FROM product");
      st.execute("DELETE FROM raw_material");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
