ZenbuTorrent
=============
A powerful, two-part Java torrent library with minimal dependencies.

Info
============
ZenbuTorrent is divided into two main parts, local and remote. Both of these are WIP and are not complete yet.

Primary mercurial repo: [Bitbucket](https://bitbucket.org/Ippytraxx/zenbutorrent/overview)

Git mirror: [GitHub](https://github.com/Ippytraxx/ZenbuTorrent)

Local
------------
The local part is a "native" Java library that aims to implement the Bitorrent protocol and many of its extensions with Java code.

Remote
------------
The remote part is a connector that wraps many other popular Bitorrent client APIs. The remote library can be used to control clients such
as uTorrent or Transmission.

Todo
---------
* Add wrappers for other torrent clients (if you want to help out, please write a wrapper, just implement the ClientWrapper interface)
    * ~~uTorrent~~
    * ~~Transmission~~
    * ~~Deluge~~
    * QBittorrent - (https://github.com/qbittorrent/qBittorrent/wiki/WebUI-API-Documentation)
    * Vuze - (http://wiki.vuze.com/w/XML_over_HTTP)
    * rTorrent - (http://libtorrent.rakshasa.no/wiki/RTorrentXMLRPCGuide)
* One day in the distant future, finish the local part
