Lightweight library with minimal dependencies that wraps common torrent clients for easy use with Java.

[Javadocs 0.0.1](https://ippytraxx.github.io/ZenbuTorrent)

__Quickstart__

	ClientWrapper wrapper = new TransmissionWrapper("user", "pass");
	
	wrapper.addTorrent("http://releases.ubuntu.com/14.04.1/ubuntu-14.04.1-desktop-amd64.iso.torrent");
	
	List<Torrent> torrents = wrapper.getTorrents();

	torrents.stream().forEach(System.out::println);
	
	Torrent torrent = torrents.get(0);
	torrent.pause();
	