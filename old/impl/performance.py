import time
import operator

class Performance:
    def __init__(self):
        self.startT = 0
        self.endT = 0
        self.diffT = 0
        self.dict = {}

    def start(self, *args):
        st = time.process_time()
        if len(args) == 0:
            self.startT = st
        elif args[0] in self.dict:
            self.dict[args[0]].setStart(st)
        else:
            pu = PerformanceUnit()
            pu.setStart(st)
            self.dict[args[0]] = pu

    def end(self, *args):
        st = time.process_time()
        if len(args) == 0:
            self.endT = st
            self.diff = self.endT - self.startT
        else:
            self.dict[args[0]].setEnd(st)

    def perc(self, value1, value2):
        return ((value1 * 100.0) / value2)

    def printSummary(self):
        sortedDict = sorted(self.dict.items(), \
            key = operator.itemgetter(1), reverse = True)
        print(format('module', '20'), format('percent', '16'), \
            format('time', '20'))
        print('--------------------------------------------------')
        totalTime = 0.0
        totalPercent = 0.0
        for sd in sortedDict:
            percent = self.perc(sd[1].diff, self.diff)
            totalPercent += percent
            percent = format(percent, '.2f')
            time = sd[1].diff * 1000
            totalTime += time
            time = format(time, '.7f')
            print(format(sd[0], '20'), format(percent + ' %', '16'), \
                format(time + ' ms', '20'))
        print('--------------------------------------------------')
        totalTime = format(totalTime, '.7f')
        totalTime = format(totalTime + ' ms', '20')
        totalPercent = format(totalPercent, '.2f')
        totalPercent = format(totalPercent + ' %', '16')
        print(format('Sum modules time', '20'), totalPercent, totalTime)
        print(format('Total time', '20'), format('100 %', '16'), \
            format(format(self.diff * 1000, '.7f') + ' ms', '20'))

class PerformanceUnit:
    def __init__(self):
        self.start = 0
        self.diff = 0

    def setStart(self, start):
        self.start = start

    def setEnd(self, end):
        self.diff += end - self.start

    def __lt__(self, other):
         return self.diff < other.diff