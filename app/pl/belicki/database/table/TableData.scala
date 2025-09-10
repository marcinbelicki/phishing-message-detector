package pl.belicki.database.table

import pl.belicki.database.DatabaseConfiguration
import slick.jdbc.JdbcProfile
import slick.lifted.{AbstractTable, TableQuery}

trait TableData {
  def databaseConfiguration: DatabaseConfiguration
  lazy val profile: JdbcProfile = databaseConfiguration.profile
  type M
  type T <: AbstractTable[M]
  def query: TableQuery[T]
}
