def attach_to_event(subject, observer):
    subject.observers.append(observer)

def detach_from_event(subject, observer):
    subject.observers.remove(observer)

def event(func):
    def inner(*args, **kwargs):
        func(*args, **kwargs)
        for observer in inner.observers:
            observer(*args, **kwargs)

    inner.observers = []
    return inner

@event
def some_event(data, data2):
    print("some_event " + str(data) + str(data2))

def foo(data, data2):
    print("foo " + str(data) + str(data2))

def foo2(data, data2):
    print("foo2 " + str(data))

attach_to_event(some_event, foo)
attach_to_event(some_event, foo2)
some_event("test", " wiadomosc")
detach_from_event(some_event, foo)
some_event("test", " wiadomosc")
