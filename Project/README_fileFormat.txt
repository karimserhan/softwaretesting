The file will contain a line for each process P0, P1, ..., Pn.

Each line will be formatted as such

Pi: 0(T), 1(T), 2(F), ....
The numbers are the IDs of the events, and the letter in parenthesis indicates the value of the local predicate when the event started executing.

After the lines for each process, we list the messages exchanged in the computation. Each message will be on a single line, and will be formatted as
e,f

where e is the ID of the send event, and f is the ID of the receive event.