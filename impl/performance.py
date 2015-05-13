import time
import operator

class Performance:
    def __init__(self):
        self.startT = 0
        self.endT = 0
        self.diffT = 0
        self.dict = {}

    def start(self, *args):
        if len(args) == 0:
            self.startT = time.process_time()
        else:
            pu = PerformanceUnit()
            pu.setStart(time.process_time())
            self.dict[args[0]] = pu

    def end(self, *args):
        if len(args) == 0:
            self.endT = time.process_time()
            self.diff = self.endT - self.startT
        else:
            self.dict[args[0]].setEnd(time.process_time())

    def perc(self, value1, value2):
        return ((value1 * 100.0) / value2)

    def printSummary(self):
        sortedDict = sorted(self.dict.items(), \
            key = operator.itemgetter(1), reverse = True)
        print(format('module', '20'), format('percent', '16'), \
            format('time', '20'))
        print('--------------------------------------------------')
        for sd in sortedDict:
            percent = self.perc(sd[1].diff, self.diff)
            percent = format(percent, '.2f')
            time = sd[1].diff * 1000
            time = format(time, '.7f')
            print(format(sd[0], '20'), format(percent + ' %', '16'), \
                format(time + ' ms', '20'))

class PerformanceUnit:
    def __init__(self):
        self.start = 0
        self.end = 0
        self.diff = 0

    def setStart(self, start):
        self.start = start

    def setEnd(self, end):
        self.end = end
        self.diff = self.end - self.start

    def __lt__(self, other):
         return self.diff < other.diff