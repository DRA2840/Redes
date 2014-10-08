Proxy
===============================

Idéia Geral:

 - Recebe uma whitelist/blacklist
 - Abre um socket para ouvir requisicoes
 - Quando chegar uma requisicao:
 - - Verificar na white/black list
 - - Anotar o pedido se for negado (página requisitada e IP)
 - - Fazer a requisicao anyway
 - - Devolver pro usuário se for o caso
 