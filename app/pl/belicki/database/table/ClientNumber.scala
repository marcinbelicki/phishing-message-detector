package pl.belicki.database.table

import com.google.inject.Inject
import pl.belicki.database.DatabaseConfiguration

class ClientNumber @Inject()(val databaseConfiguration: DatabaseConfiguration)
    extends TableData {
  import profile.api._

  class Schema(tag: Tag) extends Table[String](tag, "client_number") {
    def number     = column[String]("number")
    override def * = number
  }

  override type M = String
  override type T = Schema

  override lazy val query: TableQuery[Schema] = TableQuery[Schema]

}
