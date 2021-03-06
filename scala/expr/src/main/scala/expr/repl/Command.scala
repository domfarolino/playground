package expr.repl

abstract sealed class Command

object Command {
  case class Exit() extends Command
  case class ShowEnv() extends Command
  case class Help() extends Command
  case class Eval(expr: Expr) extends Command
  case class Assign(variable: String, expr: Expr) extends Command
  case class Define(name: String, lambda: Lambda) extends Command

  def parse(input: String): Command = {
    val parser = new CommandParser
    val result = parser.parseAll(parser.command, input)

    result.getOrElse { throw new BadInputException("Cannot be parsed") }
  }

  class CommandParser extends expr.Parser {
    def command = exit | help | showEnv | define | assign | eval
    def define = ident ~ "=" ~ "lambda" ~ "(" ~ repsep(ident, ",") ~ ")" ~ "{" ~ expr ~ "}" ^^ {
      case name ~ "=" ~ "lambda" ~ "(" ~ args ~ ")" ~ "{" ~ expr ~ "}" => Define(name, Lambda(args, expr))
    }
    def assign = ident ~ "=" ~ expr ^^ { case variable ~ "=" ~ expr => Assign(variable, expr) }
    def help = "help" ^^ { case _ => Help() }
    def exit = "exit" ^^ { case _ => Exit() }
    def showEnv = "names" ^^ { case _ => ShowEnv() }
    def eval = expr ^^ { case expr => Eval(expr) }
  }
}

