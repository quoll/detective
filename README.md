# Detective

A proposed solution to a "Detective" problem of merging eyewitness timelines.

## Problem Statement

A detective is asked to review timelines from a number of witnesses. Each recalls the order of
the events that they witnessed, and they are all trustworthy. However, witnesses may not have
witnessed or recall witnessing every event.

For instance:

    Lisa remembers: shouting, fight, fleeing
    Mike remembers: fight, gunshot, panic, fleeing
    Ned remembers: anger, shouting

The detective needs to construct a maximal timeline from each of these witness timelines. When
enough of the witness timelines can be merged to form long enough timeline, then this increases
the likelihood of a successful conviction. The ordering of events must be absolutely correct,
or else the case will be thrown out. If timelines cannot be strictly ordered, then multiple
timelines must be presented.

- If all witnesses remember evenents in a fully consistent manner, then present a single merged timeline
- if some of the events they rememebr can be combined, or if some of the them can be extended
without fully mergint them, then present multiple timelines with events merged across them
to the maximum degree possible.
- if none of the events can be combined, or extended, then present the original, unmodified timeslines

The above example can be combined into a single timeline:

    anger, shouting, fight, gunshot, panic, fleeing

An example of multiple possible timeslines is:

    Oscar: pouring gas, laughing, lighting match fire
    Peter: buying far, pouring gas, crying, fire, smoke

Since it is not possible to tell if the crying occurred before or after lighting the match, then
two timelines emerge:

    buying gas, pouring gas, laughing, lighting match, fire, smoke
    buying gas, pouring gas, crying, fire, smoke

## Problem Format

Write a program that accepts JSON data representing eyewitness accounts, and outputs JSON to
represent the maximally merged timelines.

The input appears as an array of eyewitness accounts.  Each eyewitness account is represented
as an array of strings.

The output will also be an array of maximal timelines. Each timeline is an array of strings.

Examples:

<table>
  <tr>
    <td>Simple merge</td>
    <td>[ ["fight", "gunshot" "fleeing"], ["gunshot", "falling", "fleeing"] ]</td>
    <td>[ ["fight", "gunshot", "falling", "fleeing"] ]</td>
  </tr>
  <tr>
    <td>Partial merge</td>
    <td>[ ["shadowy figure", "demands", "scream", "siren"], ["shadowy figure", "pointed gun", "scream"] ]</td>
    <td>[ ["shadowy figure", "demand", "scream", "siren"], ["shadowy figure", "pointed gun", "scream", "siren"] ]</td>
  </tr>
  <tr>
    <td>Unable to merge</td>
    <td>[ ["argument", "stuff", "pointing"], 
    ["press brief", "scandal", "pointing"], ["bribe", "coverup"] ]</td>
    <td>[ ["argument", "stuff", "pointing"], ["press brief", "scandal", "pointing"], ["bribe", "coverup"] ]</td>
  </tr>
</table>

These examples are provided in the files example1.json, example2.json, example3.json. A more complex test is available in example4.json.

## Usage

This is a standalone program for processing JSON text files.

The program is pre-compiled in the jar: `target/detective-0.1.0-standalone.jar`

The program can be run with the command:

`java -jar target/detective-0.1.0-standalone.jar [filename]`

Where the _filename_ is an optional parameter containing the file to be processed.
If no filename is provided, then stdin will be read instead. So the following lines are equivalent:

`java -jar target/detective-0.1.0-standalone.jar example1.json`
`java -jar target/detective-0.1.0-standalone.jar < example1.json`

If you have "Leiningen" installed, then the code can be compiled and run with:

`lein run [filename]`

The standalone jar can be re-created with the command:

`lein uberjar`

There are also integration tests for all the provided examples. These can be run with:

`lein test`

## License

Copyright © 2015 Paula Gearon

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

EPL is used for ease of compatibility with the Clojure language.
