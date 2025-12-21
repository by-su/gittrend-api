package com.rootbly.openpulse.common.constants

/**
 * GitHub Topics 카테고리 정의
 * 실제 데이터 분석 기반으로 17개 주요 카테고리 구성
 */
object TopicCategories {

    /**
     * 카테고리별 키워드 매핑
     * 우선순위: 위에서 아래로 (먼저 매칭되는 카테고리가 선택됨)
     */
    val CATEGORY_KEYWORDS = mapOf(
        // 1. AI & Machine Learning (가장 구체적인 AI 관련)
        "AI & Machine Learning" to listOf(
            // LLM
            "llm", "gpt", "gpt-4o", "chatgpt", "claude", "claude-ai", "claude-code", "claude-desktop",
            "gemini", "gemini-ai", "gemini-api", "anthropic", "openai", "openai-api",
            "ollama", "llama", "llama3", "deepseek", "mistral", "qwen", "qwen3",
            // AI 일반
            "ai", "artificial-intelligence", "machine-learning", "ml",
            "deep-learning", "neural-network", "neural-networks", "deep-neural-networks",
            "convolutional-neural-networks", "cnn", "lstm",
            // 프레임워크
            "pytorch", "tensorflow", "keras", "transformers", "huggingface",
            // 기술
            "nlp", "natural-language-processing", "computer-vision", "cv",
            "reinforcement-learning", "embeddings", "bert", "yolo",
            "sentiment-analysis", "text-classification", "image-classification",
            "object-detection", "ocr", "speech-recognition", "speech-to-text",
            "text-to-speech", "tts", "whisper", "stable-diffusion", "diffusion",
            "diffusion-models", "image-generation", "generative-ai", "genai", "gen-ai",
            "anomaly-detection", "face-recognition", "pose-estimation",
            "supervised-learning", "unsupervised-learning", "reinforcement-learning",
            "transfer-learning", "data-augmentation", "feature-extraction",
            "image-segmentation", "semantic-segmentation", "word2vec",
            "named-entity-recognition", "sentiment-classification",
            "voice-recognition", "voice-conversion", "voice-cloning",
            "text-to-image", "text-to-image-generation", "text-to-video",
            "gan", "generative-adversarial-network", "generative-model",
            "attention-mechanism", "transformer-models",
            // Medical AI
            "medical-ai", "medical-image-processing", "disease-prediction",
            // Model
            "model-training", "fine-tuning", "onnx", "mlx", "mediapipe", "mlflow"
        ),

        // 2. AI Tools & Integration (AI 활용 도구)
        "AI Tools & Integration" to listOf(
            // Agent
            "ai-agent", "ai-agents", "agent", "agents", "agentic", "agentic-ai",
            "agentic-workflow", "autonomous-agents", "multi-agent", "multi-agent-system",
            "aiagent", "aiagents", "llm-agent", "llm-agents",
            "sub-agents", "subagents", "agent-framework", "ai-agents-framework",
            "agentic-framework", "agentic-ai-development", "agent-based-modeling",
            // RAG & Tools
            "rag", "retrieval-augmented-generation", "langchain", "langgraph",
            "langchain-python", "langchain4j",
            "mcp", "mcp-server", "mcp-client", "mcp-tools", "model-context-protocol",
            "mcp-servers", "mcp-gateway", "mcp-host", "mcp-clients",
            "model-context-protocol-servers", "model-context-protocol-server",
            "openrouter", "mistral",
            // AI 도구
            "ai-tools", "ai-assistant", "ai-chatbot", "chatbot", "chatbots",
            "prompt-engineering", "prompt", "prompts", "conversational-ai",
            "ai-integration", "ai-powered", "ai-memory",
            "context-engineering", "system-prompts", "prompting",
            // 평가/운영
            "llm-evaluation", "llm-inference", "llmops", "mlops", "aiops",
            "ai-integration", "semantic-search", "vector-search", "vector-database",
            "vector", "chromadb", "pinecone", "faiss", "qdrant", "milvus",
            "language-models", "local-llm",
            // AI Safety & Ethics
            "ai-safety", "ai-ethics", "ai-consciousness", "ai-philosophy",
            // Specific Tools
            "cursor-ai", "cursor-ide", "cursors", "cursor-ai-editor",
            "windsurf-ai", "cursorai", "cline",
            "groq", "groq-api", "perplexity",
            "claude-skills", "claude-code-plugin", "claude-code-subagents", "claude-code-hooks",
            "gemini-cli", "google-gemini",
            // Frameworks
            "llm-framework", "llm-gateway", "ai-gateway", "ai-framework",
            "llm-observability", "guardrails"
        ),

        // 3. Programming Languages
        "Programming Languages" to listOf(
            "python", "python3", "python-library", "python-script", "python-3",
            "python-lang", "python-language",
            "javascript", "js", "javascipt", "typescript", "ts", "nodejs", "node", "node-js",
            "java", "kotlin", "kotlin-android", "kotlin-multiplatform", "kotlin-js", "kotlin-flow",
            "rust", "rust-lang", "rust-library", "go", "golang", "pure-go",
            "cpp", "c-plus-plus", "c", "cpp20", "cpp23", "modern-cpp", "cpp-library",
            "csharp", "c-sharp", "csharp-library",
            "php", "php-fpm", "php-framework", "ruby", "rails", "swift", "dart",
            "lua", "luajit", "perl", "perl5", "scala", "groovy", "groovy-script",
            "haskell", "elixir", "erlang", "julia", "ocaml", "common-lisp",
            "clojure", "clojurescript", "scheme",
            "objective-c", "r", "rstats", "r-packages", "cran",
            "assembly", "x64", "aarch64", "riscv",
            "matlab", "octave", "fortran",
            "verilog", "vhdl", "systemverilog",
            "bash", "shell", "zsh", "fish", "linux-shell",
            "dotnet", ".net", "netcore", "asp-net", "asp-net-core", "aspnetcore",
            "zig", "no-std",
            "programming", "programming-language", "programming-languages"
        ),

        // 4. Web Development
        "Web Development" to listOf(
            // React 생태계
            "react", "reactjs", "react-native", "react-hooks", "react-router",
            "react-router-dom", "react-redux", "react-query", "react-bootstrap",
            "react-components", "react-component", "react-three-fiber",
            "react-template", "react19", "create-react-app",
            "nextjs", "nextjs14", "nextjs15", "nextjs16", "nextjs-template", "nextjs-typescript",
            "remix",
            // Vue 생태계
            "vue", "vuejs", "vue3", "vue3-composition-api", "nuxt", "nuxt3", "sveltekit", "svelte5",
            // 기타 프레임워크
            "angular", "svelte", "astro", "astrojs", "solidjs",
            // 기본
            "html", "html5", "html-css", "html-css-javascript",
            "css", "css3", "css-lang", "css-language", "css-modules",
            // CSS 프레임워크
            "tailwind", "tailwindcss", "tailwind-css", "tailwindcss-v4", "bootstrap", "bootstrap5", "sass", "scss",
            "material-design", "material-ui", "material3", "shadcn-ui", "shadcn", "daisyui",
            "styled-components", "ant-design", "antd", "chakra-ui", "heroui",
            "css-framework",
            // 도구
            "vite", "webpack", "rollup", "bun", "pnpm", "npm", "npm-package",
            "turborepo", "yarn",
            // 일반
            "web", "web-development", "web-app", "webapp", "website",
            "frontend", "frontend-development", "backend", "backend-development",
            "fullstack", "full-stack", "ssr", "pwa", "spa", "single-page-app",
            "responsive", "responsive-design", "ui", "ui-components", "component-library",
            "ui-library", "ui-kit", "headless", "headless-cms",
            "web-components", "webcomponents", "web-dashboard",
            // UI/UX
            "ui-design", "dark-mode", "dark-theme",
            // 프레임워크
            "express", "expressjs", "fastapi", "fastify", "flask", "flask-api", "flask-application",
            "django", "django-rest-framework", "django-admin", "laravel", "laravel-package", "laravel-nova",
            "spring-boot", "springboot", "java-spring-boot", "springboot2",
            "spring", "spring-cloud", "spring-security", "spring-mvc", "spring-ai",
            "nestjs", "phoenix", "symfony", "asp-net-core", "aspnetcore",
            "hono", "gin", "fiber", "echo",
            // 기타
            "htmx", "alpinejs", "lucide-react", "recharts", "tanstack-query",
            "context-api", "zustand", "swr", "react-toastify",
            "jquery", "jquery-plugin", "ajax", "fetch", "fetch-api", "axios",
            "websocket", "socket-io", "server-sent-events",
            "web-assembly", "wasm", "webgl", "webgl2",
            "progressive-web-app", "pwa", "offline-support", "offline-first",
            "hot-reload", "hmr",
            "content-management", "cms", "cms-framework", "content-management-system",
            "webui", "web-tools", "web-development-tools", "webdev",
            "single-page-app", "spa", "server-side-rendering", "ssr",
            "layout", "responsive-ui", "mobile-first",
            "animation-css", "svg-animations",
            "web-standards", "whatwg", "wcag",
            "website-template", "website-builder", "web-dashboard",
            "feature-flags", "feature-toggles",
            "cdn", "cors", "session", "custom-session",
            "routing", "router",
            "api-documentation", "swagger-ui", "openapi", "openapi31",
            "webscraping", "web-scraper", "web-automation", "scrapy",
            "lighthouse", "performance-tuning",
            "blade", "ejs", "pug", "handlebars", "mustache", "templating",
            "hugo", "jekyll", "gatsby", "11ty", "pelican",
            "stenciljs", "polymer", "lit", "web-components",
            "meteor", "sails", "adonis", "feathers", "loopback",
            "codeigniter", "cakephp", "yii", "slim", "lumen",
            "play-framework", "vertx", "micronaut", "quarkus",
            "rails", "sinatra", "padrino", "hanami",
            "asp-net", "blazor-server", "blazor-webassembly", "razor", "winui",
            "xamarin", "maui", "xaml",
            "vaadin", "gwt", "wicket", "tapestry",
            "odoo", "magento", "magento2-extension"
        ),

        // 5. Mobile Development
        "Mobile Development" to listOf(
            "mobile", "mobile-app", "mobile-apps", "mobile-development", "mobile-game", "mobile-gaming",
            "android", "android-app", "android-studio", "android-automation",
            "android-ui", "android-library", "android-security",
            "ios", "swift", "swiftui", "xcode", "macos", "macos-app", "apple", "apple-silicon",
            "watchos", "tvos", "visionos",
            "flutter", "flutter-apps", "flutter-package", "flutter-plugin", "flutter-web",
            "react-native",
            "kotlin-android", "kotlin-multiplatform", "compose-multiplatform",
            "jetpack-compose", "jetpack", "livedata", "databinding",
            "expo", "cordova",
            "appium", "appilot", "ui-automator",
            "xamarin", "maui",
            "ionic", "capacitor", "nativescript",
            "iphone", "ipad", "playstore", "appstore",
            "ios-app", "mobile-first",
            "file-transfer-android", "adb-less-control",
            "haptic-feedback", "biometrics",
            "push-notifications", "fcm", "apns"
        ),

        // 6. DevOps & Infrastructure
        "DevOps & Infrastructure" to listOf(
            // 컨테이너
            "docker", "docker-compose", "docker-image", "docker-container", "docker-swarm",
            "dockerfile", "dockerhub", "docker-registry",
            "kubernetes", "k8s", "k8s-at-home", "k3s", "helm", "helm-chart", "helm-charts",
            "kubernetes-operator", "kubernetes-controller", "kubernetes-cluster",
            "kustomize", "rancher", "portainer",
            "containers", "container", "containerization",
            // CI/CD
            "devops", "cicd", "ci-cd", "ci",
            "github-actions", "github-action", "actions", "gitlab", "jenkins", "jenkins-cft",
            "continuous-integration", "continuous-delivery", "continuous-deployment",
            // GitOps
            "gitops", "argocd", "flux", "fluxcd", "renovate",
            // IaC
            "terraform", "terraform-provider", "ansible",
            "infrastructure-as-code", "infrastructure",
            // 모니터링
            "monitoring", "observability", "prometheus", "grafana", "grafana-dashboard",
            "metrics", "logging", "telemetry", "tracing", "distributed-tracing",
            "opentelemetry", "apm", "datadog", "siem", "loki", "victoriametrics",
            "sentry", "crash-reporting", "error-monitoring",
            "splunk", "elk", "logstash", "kibana",
            "monitoring-tools", "monitoring-tool", "network-monitoring",
            // 클라우드
            "cloud", "cloud-computing", "cloud-native", "serverless", "cloud-deployment",
            "cloud-security", "edge-computing", "multi-cloud",
            "aws", "aws-lambda", "aws-s3", "aws-ec2", "aws-sdk", "aws-dynamodb", "aws-bedrock",
            "amazon-web-services", "amazon-s3", "cloudformation", "cloudtrail",
            "azure", "azure-openai", "azure-devops", "azure-sdk",
            "gcp", "google-cloud", "google-adk",
            "heroku", "vercel", "netlify", "cloudflare", "cloudflare-pages",
            "digitalocean", "linode", "scaleway",
            "paas", "iaas", "saas",
            "openshift", "nomad", "consul", "vault", "hashicorp-vault", "hashicorp",
            "render-deployment",
            // 오케스트레이션
            "orchestration", "workflow", "workflows", "workflow-automation",
            "workflow-engine", "airflow", "apache-airflow", "n8n", "n8n-workflow",
            "temporal", "cadence", "dagster", "prefect", "luigi",
            "kestra", "argo", "argo-workflows",
            // 서비스 메시
            "service-mesh", "istio", "linkerd", "consul", "envoy",
            "api-gateway", "gateway-api", "ingress-controller", "kong",
            // 자동화
            "automation", "devops-automation", "devops-tools",
            "ansible-role", "ansible-playbook", "playbook",
            "terraform-module", "terraform-provider",
            "pulumi", "crossplane",
            // 스케일링
            "autoscaling", "load-balancer", "load-balancing", "high-availability",
            "fault-tolerance", "resiliency", "disaster-recovery",
            // 네트워크
            "networking", "network", "vpn", "vpn-manager", "dns", "dhcp",
            "proxy", "reverse-proxy", "nginx", "caddy", "traefik", "haproxy",
            "iptables", "firewall", "fail2ban", "security-groups",
            "vlan", "inter-vlan-routing", "ivr", "network-simulation",
            "network-analysis", "network-scanner",
            // 가상화
            "virtualization", "vm", "virtual-machine", "kvm", "qemu",
            "vmware", "virtualbox", "hypervisor", "esxi",
            "proxmox", "proxmox-ve", "lxc", "lxd",
            // 백업/복구
            "backup", "restore", "recovery", "disaster-recovery",
            "snapshot", "replication", "data-replication", "sync",
            // 시크릿 관리
            "secrets", "secrets-management", "secret", "vault",
            "environment-variables", "env", "dotenv", "config",
            "key-management", "certificate", "tls-certificate", "ssl", "pki",
            "acme", "letsencrypt", "x509",
            // 플랫폼
            "platform-engineering", "internal-tools", "backstage",
            // Linux/Unix
            "linux", "linux-kernel", "ubuntu", "debian", "centos", "rhel", "fedora",
            "arch", "arch-linux", "aur",
            "alpine", "alpine-linux", "busybox",
            "unix", "bsd", "freebsd", "openbsd", "osx", "macos",
            "openwrt", "talos-linux",
            // 기타
            "deployment", "deploy", "configuration-management", "configuration",
            "baremetal", "bare-metal", "edge", "iot", "iiot",
            "homelab", "homelab-setup", "home-ops", "self-hosting",
            "sre", "reliability-engineering", "chaos-engineering",
            "gitops", "argocd", "flux", "flux2", "fluxcd", "renovate",
            "registry", "harbor", "nexus", "artifactory",
            "bastion", "jumphost", "ssh", "sftp", "telnet",
            "systemd", "init", "service", "daemon",
            "cron", "crontab", "task-scheduling", "scheduler",
            "process-management", "supervisor", "pm2",
            "package-management", "package-manager",
            "compliance", "audit", "auditing", "governance", "iso27001",
            "sbom", "attestation", "provenance"
        ),

        // 7. Database & Data Storage
        "Database & Data Storage" to listOf(
            // SQL
            "database", "database-management", "sql", "sql-database",
            "postgresql", "postgres", "postgresql-database", "mysql", "mariadb",
            "sqlite", "sqlite3", "sql-server", "mssql", "oracle",
            "cassandra", "rethinkdb", "tidb",
            // NoSQL
            "mongodb", "mongodb-atlas", "redis", "elasticsearch", "opensearch",
            "dynamodb", "clickhouse", "influxdb", "nosql",
            // ORM/쿼리
            "prisma", "drizzle-orm", "drizzle", "orm", "sqlalchemy", "mongoose",
            "query-optimization", "gorm", "jpa", "spring-data-jpa",
            // 벡터/임베딩
            "vector-database", "chromadb", "pinecone", "faiss", "qdrant", "milvus",
            "graph-database", "neo4j",
            // Data Warehouse
            "data-warehouse", "snowflake", "bigquery", "redshift",
            "clickhouse", "lakehouse", "iceberg", "databricks",
            // 기타
            "s3", "aws-s3", "s3-bucket", "cloud-storage", "storage", "data-storage",
            "cache", "caching", "firestore", "firebase-realtime-database",
            "benchmark", "amd", "duckdb"
        ),

        // 8. Data Science & Analytics
        "Data Science & Analytics" to listOf(
            // 기본
            "data-science", "data-analysis", "data-analytics", "analytics",
            "data-visualization", "visualization",
            // 라이브러리
            "pandas", "pandas-dataframe", "pandas-python", "numpy", "ndarray",
            "matplotlib", "matplotlib-pyplot", "seaborn", "plotly",
            "scikit-learn", "scipy", "opencv", "opencv-python",
            // 도구
            "jupyter-notebook", "jupyter", "jupyterlab", "notebook",
            "streamlit", "streamlit-webapp", "streamlit-dashboard", "streamlit-application",
            "gradio", "tableau", "powerbi", "powerbi-report",
            // 처리
            "data-processing", "data-engineering", "data-pipelines", "data-pipeline",
            "etl", "etl-pipeline", "elt", "pipeline", "pipelines",
            "data-cleaning", "data-mining", "data-quality", "data-validation",
            "data-transformation", "data-augmentation",
            // 통계/분석
            "statistics", "exploratory-data-analysis", "eda",
            "feature-engineering", "feature-extraction",
            "regression", "logistic-regression", "linear-regression",
            "random-forest", "xgboost", "lightgbm", "catboost",
            "classification", "clustering", "kmeans", "knn",
            "predictive-modeling", "forecasting", "prediction",
            "time-series", "time-series-analysis", "time-series-database", "tsdb",
            "recommender-system", "recommendation-system", "collaborative-filtering",
            "correlation-analysis", "pca", "pca-analysis",
            "decision-trees", "gradient-boosting", "ensemble-learning",
            // 빅데이터
            "big-data", "bigdata", "bigquery", "redshift", "snowflake", "spark", "pyspark",
            "kafka", "apache-kafka", "kafka-producer", "kafka-consumer",
            "dbt", "duckdb", "dataops", "airflow", "apache-airflow",
            "hadoop", "hdfs", "flink",
            // 데이터셋
            "dataset", "datasets", "dataframe", "ndarray", "arrow", "parquet",
            "tabular-data"
        ),

        // 9. Blockchain & Crypto
        "Blockchain & Crypto" to listOf(
            // 블록체인
            "blockchain", "blockchain-technology", "blockchain-game",
            "web3", "dapp", "defi",
            // 체인
            "ethereum", "eth", "ethereum-dapp", "ethereum-contract", "ethereum-api",
            "bitcoin", "btc", "bitcoin-cash", "solana", "bnb", "bsc", "bnb-chain",
            "litecoin", "evm", "optimism", "arbitrum", "polygon", "base",
            "cardano", "polkadot", "cosmos", "ripple", "tron", "ton",
            // 기술
            "smart-contracts", "smart-contract", "smartcontract", "solidity",
            "hardhat", "truffle", "foundry", "anchor", "ganache",
            "nft", "token", "crypto-token", "erc20",
            "amm", "liquidity", "staking", "minting",
            // DeFi
            "defi", "dex", "flash-loan", "flashloan", "swap",
            "uniswap", "raydium", "meteora", "polymarket",
            "chainlink", "oracle",
            // 도구/지갑
            "wallet", "crypto-wallet", "cold-wallet", "hardware-wallet", "wallet-security",
            "metamask", "metamask-wallet", "metamask-extension", "metamask-plugin",
            "metamask-snap", "metamask-bot", "metamask-desktop",
            "phantom-wallet", "phantom-wallet-api", "phantom-wallet-bot",
            "walletconnect", "exodus", "exodus-wallet", "exodus-api",
            "exodus-app", "exodus-payment",
            // 거래/금융
            "crypto", "cryptocurrency", "cryptocurrencies", "cryptocurrency-trading",
            "cryptocurrency-exchanges", "cash",
            "trading", "trading-bot", "crypto-trading", "crypto-bot",
            "arbitrage", "mev", "dex", "flash-loan",
            "trading-strategies", "trading-algorithms", "algorithmic-trading",
            "investment-strategies", "quantitative-trading",
            "binance", "binance-api", "protocol",
            "sniper-bot", "mevbots",
            // 게임
            "blockchain-game", "crypto-game", "cryptocurrency-game", "ethereum-game",
            "play-to-earn",
            // 기타
            "web3", "web3js", "dapp", "dapps", "etherscan", "ethersjs",
            "multisig", "proof-of-work", "consensus", "decentralization",
            "decentralized", "decentralized-ai",
            "memecoin", "coingecko",
            // 게임
            "blockchain-game", "crypto-game", "cryptocurrency-game", "ethereum-game",
            "play-to-earn"
        ),

        // 10. Security & Hacking
        "Security & Hacking" to listOf(
            // 보안 일반
            "security", "cybersecurity", "cyber-security", "infosec",
            "security-tools", "security-tool", "security-research",
            "devsecops", "network-security", "cloud-security", "windows-security",
            // 인증/암호화
            "authentication", "authorization", "auth", "oauth", "oauth2",
            "2fa", "jwt", "oidc", "sso",
            "encryption", "cryptography", "aes", "sha256", "hash",
            "access-control", "active-directory", "iam", "identity",
            "user-authentication", "user-management", "permissions",
            "rbac",
            // 공격/침투
            "hacking", "hack", "ethical-hacking", "pentesting", "penetration-testing",
            "pentest", "pentest-tool", "pentesting-tools",
            "exploit", "exploit-development", "vulnerability",
            "vulnerability-assessment", "vulnerability-management", "vulnerability-detection",
            "vulnerability-scanner", "vulnerability-scanning", "cve", "cve-scanning",
            "malware", "malware-analysis", "malware-detection", "malware-development",
            "malware-research", "ransomware", "backdoor", "trojan",
            "reverse-engineering", "obfuscation",
            "dll-injection", "dll-hijacking", "dll-hooking", "injection",
            "privilege-escalation", "evasion", "evasion-techniques",
            "av-evasion", "edr", "edr-bypass",
            "shellcode", "shellcode-injection", "payload", "payload-encryption",
            "in-memory-execution", "thread-injection",
            // Windows Security
            "windows-api", "windows-internals", "windows-malware", "windows-security",
            "windows-exploitation",
            // 도구/기법
            "osint", "osint-tool", "osint-resources", "reconnaissance", "recon",
            "red-team", "redteam", "red-teaming", "blue-team", "blueteam",
            "offensive-security", "threat-intelligence", "threatintel", "threat-hunting",
            "threat-detection", "incident-response", "forensics", "dfir", "digital-forensics",
            "fuzzing", "phishing", "phishing-detection",
            "proxy", "vpn", "anonymity", "anonymous", "privacy", "privacy-tools", "privacy-protection",
            "tor", "censorship", "siem",
            // 특정 도구
            "metasploit", "cobalt-strike", "nmap", "hydra", "suricata", "zeek", "frida",
            // MITRE
            "mitre-attack",
            // 네트워크 보안
            "network-scanner", "network-monitoring", "network-analysis"
        ),

        // 11. Proxy & Network Tools
        "Proxy & Network Tools" to listOf(
            // VPN/프록시
            "vpn", "proxy", "proxies", "proxy-server", "proxy-list",
            "proxy-scraper", "proxy-checker", "free-proxy", "free-proxy-list",
            "http-proxy", "https-proxy", "socks", "socks4", "socks4-proxy",
            "socks5", "socks5-proxy", "socks-proxy",
            // 중국 도구
            "shadowsocks", "v2ray", "v2rayn", "v2rayng", "vmess", "vless",
            "clash", "wireguard", "openvpn", "trojan",
            // 네트워크
            "networking", "network", "network-security", "dns", "dns-server",
            "firewall", "gateway", "reverse-proxy", "traefik", "nginx",
            // 우회
            "gfw", "censorship", "china", "chinese-communist-party", "totalitarian",
            "falun-gong", "iran", "human-rights"
        ),

        // 12. Bot & Automation
        "Bot & Automation" to listOf(
            "bot", "bots", "automation", "automation-tools", "automation-scripts",
            // Telegram
            "telegram", "telegram-bot", "telegrambot", "telegram-bots",
            "telegram-proxy", "telegram-store", "telegram-store-bot",
            "telethon", "aiogram",
            // Discord
            "discord", "discord-bot", "discordbot", "discord-js", "discord-py",
            "discord-api", "discord-bots", "discord-bot-framework",
            "discord-bot-template", "discord-bot-dashboard",
            // 기타 플랫폼
            "chatbot", "chatbots", "ai-chatbot", "chatbot-development",
            "conversational-ai", "conversational-agents",
            "whatsapp", "whatsapp-api", "facebook", "twitter", "twitter-bot",
            "instagram", "instagram-api", "tiktok",
            "slack-bot", "slackbot",
            // 자동화
            "auto", "auto-clicker", "auto-farm", "game-bot", "game-cheat",
            "workflow-automation", "workflow-engine", "n8n", "n8n-workflow",
            "rss", "rss-feed", "rss-aggregator", "rss-reader", "rss-generator",
            // 소셜 미디어 자동화
            "youtube-bot", "youtube-automation",
            "quora-automation"
        ),

        // 13. API & Integration
        "API & Integration" to listOf(
            "api", "api-development", "api-integration", "api-rest", "backend-api",
            "rest", "rest-api", "restful-api", "rest-client",
            "graphql", "grpc", "rpc", "trpc", "jsonrpc",
            "microservices", "microservice", "microservices-architecture",
            "websocket", "socket-io", "socket", "sockets", "webrtc", "realtime-messaging",
            "openapi", "openapi3", "openapi31", "openapiclient", "swagger", "swagger-ui",
            "api-documentation", "api-testing", "api-gateway", "api-security",
            "sdk", "api-client", "httpclient", "http-client", "fetch", "fetch-api",
            "axios", "requests", "aiohttp", "curl",
            "protobuf", "grpc-web", "msgpack", "avro", "thrift",
            "github-api", "gitlab-api", "bitbucket-api",
            "oauth", "oauth2", "oidc", "openid-connect", "openid", "jwt", "jwt-authentication",
            "authentication-backend", "authentication-service", "auth", "auth0",
            "clerk", "fusionauth", "keycloak", "authelia",
            "webhook", "webhooks", "server-sent-events", "sse", "eventsource",
            "message-broker", "rabbitmq", "kafka", "redis-streams", "pulsar", "nats",
            "amqp", "mqtt", "stomp", "zeromq",
            "servicebus", "eventbridge", "sns", "sqs",
            "bff", "backend-for-frontend",
            "enterprise-integration", "integration-testing",
            "mq", "message-queue", "pub-sub", "pubsub",
            "coap", "xmpp",
            "json-api", "json-parser", "json", "xml", "yaml", "toml",
            "postman", "insomnia", "httpie", "apidog"
        ),

        // 14. Game Development
        "Game Development" to listOf(
            "game", "games", "gaming", "gamedev", "game-development", "game-dev",
            "game-design", "game-engine", "engine",
            // 엔진
            "unity", "unity3d", "godot", "godot4", "bevy", "raylib",
            // 장르
            "3d", "2d", "2d-game", "2d-games", "3d-game", "multiplayer", "multiplayer-game",
            "multiplayer-game-server", "mmorpg", "platformer", "rpg", "rpg-game",
            "adventure-game", "browser-game",
            // 특정 게임
            "minecraft", "minecraft-server", "minecraft-mod", "minecraft-plugin",
            "minecraft-launcher", "minecraft-bedrock-edition",
            "roblox", "pokemon", "genshin-impact",
            "fortnite", "valorant", "cs2", "csgo", "csgo2", "counter-strike", "counter-strike-2",
            "call-of-duty", "lol", "among-us", "axie-infinity", "axie-infinity-game",
            // 게임 봇/치트
            "game-bot", "game-cheat", "fivem", "fivem-cheat", "fivem-hack",
            "fivem-aimbot", "fivem-esp", "fivem-script", "fivem-spoofer",
            "valorant-cheat", "valorant-hack", "valorant-aimbot", "valorant-esp",
            "qbcore",
            // 기타
            "mini-game", "clicker-game", "simulation", "simulator",
            "physics", "physics-simulation", "physics-engine",
            "graphics", "webgl", "webgl2", "opengl", "vulkan", "shaders",
            "threejs", "canvas",
            // 게임 서버
            "game-server", "spigot-plugin", "bungeecord", "bukkit",
            // 게임 라이브러리
            "pygame", "game-library",
            // 게임 도구
            "steam", "steam-games"
        ),

        // 15. Testing & Quality
        "Testing & Quality" to listOf(
            "testing", "test", "test-automation", "testing-tools", "testing-library",
            // E2E Testing
            "selenium", "selenium-python", "playwright", "puppeteer", "cypress",
            "e2e", "e2e-testing", "end-to-end-testing",
            // Unit Testing
            "jest", "vitest", "pytest", "junit", "junit5", "mocha", "phpunit",
            "unit-test", "unit-testing", "mockito", "mock",
            // API Testing
            "api-testing", "rest-client", "postman", "swagger-ui",
            // 코드 품질
            "tdd", "bdd", "testing-tools",
            "code-quality", "code-analysis", "static-analysis", "static-code-analysis",
            "linter", "eslint", "eslint-config", "prettier", "ruff",
            "code-review", "verification", "validation",
            "benchmark", "profiling", "performance-analysis", "performance-testing",
            "performance-monitoring", "performance-metrics",
            "batch", "evaluation", "qa", "qa-automation",
            // Coverage
            "coverage", "code-coverage",
            // 기타
            "sonarcloud"
        ),

        // 16. Tools & Utilities
        "Tools & Utilities" to listOf(
            // CLI
            "cli", "cli-tool", "cli-app", "command-line", "command-line-tool",
            "terminal", "terminal-ui", "terminal-app", "shell", "bash", "bash-script",
            "zsh", "fish", "powershell", "powershell-module",
            // 에디터
            "vscode", "vscode-extension", "visual-studio-code", "visual-studio",
            "neovim", "nvim", "neovim-plugin", "neovim-config", "neovim-configuration",
            "neovim-dotfiles", "vim",
            "cursor", "cursor-ai", "cursor-ide", "cursor-ai-editor",
            "ide", "web-ide", "intellij-plugin", "intellij",
            // AI 코딩 도구
            "windsurf-ai", "cursorai", "bolt", "cline",
            "github-copilot", "coding-assistant", "coding-agents", "ai-coding",
            "agentic-coding", "code-execution",
            // 도구
            "tools", "developer-tools", "development-tools", "devtools", "dev-tools",
            "utilities", "utility", "util", "utils", "helper",
            "productivity", "productivity-tools", "productivity-tool", "productivity-app",
            "toolkit", "manager", "downloader", "calculator", "calculator-application",
            "yt-dlp", "console",
            // 버전관리
            "git", "github", "github-api", "github-pages", "github-profile",
            "github-readme", "github-readme-profile", "github-profile-readme",
            "github-stats", "github-page",
            "gitlab", "version-control", "git-scraping",
            // 패키지 관리자
            "package-manager", "package-management", "npm", "pnpm", "yarn",
            "maven", "gradle", "gradle-plugin", "pip", "pypi", "cargo",
            "homebrew", "apt", "scoop", "scoop-bucket", "scoop-apps",
            "aur", "pkgbuild", "pkgbuilds",
            // Dotfiles
            "dotfiles", "dotfiles-linux", "dotfiles-macos", "chezmoi",
            "configuration", "config", "configuration-management", "metadata",
            "nixos-config", "nixos-configuration", "nix-darwin", "nix-flake", "nix-flakes",
            "flake",
            // 문서화
            "documentation", "docs", "readme", "readme-md",
            "markdown", "markdown-lang", "markdown-language", "markdown-editor",
            "mdx", "latex", "mkdocs", "docusaurus", "docusaurus2",
            // 기타
            "addon", "plugin-manager", "extension", "plugin", "plugins",
            "screenshot", "screen-capture", "clipboard", "copy-paste",
            "anki", "anki-addon", "obsidian", "obsidian-plugin", "obsidian-md",
            "notion", "evernote", "onenote",
            "time-tracker", "time-management", "task-manager", "todo", "to-do-app",
            "todolist", "todolist-application", "tasks", "task-scheduling",
            "note", "note-taking", "notes", "notebook",
            "bookmark", "bookmarks", "bookmarklet", "read-later",
            "rss-reader", "feed-reader", "feeds",
            "calendar", "calendars", "scheduling", "appointment-booking",
            "email", "email-client", "mail", "imap", "smtp", "smtp-server",
            "launcher", "app-launcher", "spotlight", "alfred",
            "window-manager", "i3", "sway", "swaywm", "wlroots",
            "kde", "gnome", "xfce", "desktop-environment", "desktop-customization",
            "x11", "wayland", "display-server",
            "color-scheme", "color", "colors", "theme", "theming",
            "font", "font-awesome", "icons", "icon-pack",
            "widgets", "widget", "dashboard", "status-bar", "statusbar",
            "file-manager", "file-explorer", "file-transfer", "file-sharing",
            "sync", "synchronization", "backup", "restore",
            "archive", "archiver", "compression", "zip", "tar", "7zip",
            "pdf", "pdf-tools", "pdf-to-json", "docx", "document-processing",
            "converter", "conversion", "convert", "transform",
            "image-editing", "image-processing", "image-manipulation",
            "screenshot-tool", "screen-recorder", "video-editing",
            "downloader", "download-manager", "aria2",
            "media-player", "media-library", "music-player", "video-player",
            "music-library", "player",
            "translator", "translation", "i18n", "internationalization",
            "transcribe", "transcription", "captions", "subtitles",
            "ocr", "tesseract", "tesseract-ocr",
            "qr-code", "qr-generator", "barcode",
            "url-shortener", "link-shortener", "url",
            "scraper", "web-scraper", "spider", "crawler", "crawling",
            "automation-tools", "macro", "macros", "scripting",
            "utility-library", "helper-library", "common",
            "cli-framework", "cli-builder", "command-parser",
            "tui", "terminal-ui", "curses", "ncurses", "bubbletea", "ratatui",
            "ascii", "ascii-art", "unicode", "emoji",
            "random", "random-generator", "uuid", "guid",
            "hash", "hashing", "md5", "sha", "checksum",
            "diff", "diff-tool", "merge-tool", "patch",
            "linter", "formatter", "pretty-print", "beautifier",
            "installer", "setup", "install", "update", "upgrade",
            "cleaner", "cleanup", "garbage-collection",
            "monitor", "monitoring", "system-monitor", "resource-monitor",
            "logger", "log-viewer", "log-parser",
            "debug", "debugging", "debugging-tool", "debugger",
            "profiler", "profiling", "benchmark", "benchmarking",
            "test-runner", "test-harness", "test-framework",
            "mock", "mocking", "stub", "faker",
            "generator", "code-generator", "codegen", "boilerplate-generator",
            "scaffold", "scaffolding", "template-engine",
            "vim-plugin", "emacs", "emacs-lisp", "emacs-configuration",
            "tmux", "screen", "multiplexer",
            "zellij", "wezterm", "alacritty", "kitty",
            "personal-knowledge-management", "pkm", "zettelkasten",
            "flashcard", "flashcards", "spaced-repetition", "srs",
            "mnemonic", "memory-aid",
            "research-tools", "research-tool", "academic-research",
            "reference-manager", "bibliography", "citation",
            "diagram", "diagramming", "flowchart", "mind-map",
            "graphviz", "mermaid", "plantuml", "drawio",
            "whiteboard", "collaborative-whiteboard", "excalidraw",
            "presentation", "slides", "slideshow",
            "gif", "gif-maker", "gif-creator", "animation",
            "webp", "avif", "image-format",
            "user-guide", "guide", "walkthrough", "tutorial", "tutoriel",
            "cheatsheet", "cheat-sheet", "reference", "quick-reference",
            "badge", "shield", "status-badge", "build-badge"
        ),

        // 17. Finance & Trading
        "Finance & Trading" to listOf(
            "finance", "fintech", "financial", "financial-analysis",
            "trading", "trading-bot", "trading-strategies", "trading-algorithms",
            "trading-automation", "trading-platform", "trading-signal",
            "trading-signal-api", "trading-signal-bot", "trading-signals-info",
            "algorithmic-trading", "quantitative-finance", "quantitative-analysis",
            "quantitative-trading", "quant", "hft", "high-frequency-trading",
            "stock-market", "stocks", "equity", "stock-data", "stock-analysis",
            "stock-price-prediction", "price-prediction",
            "forecasting", "prediction", "backtesting",
            "investment", "investment-strategies", "portfolio-management",
            "portfolio", "asset-management", "wealth-management",
            "accounting", "budgeting", "budget", "money-tracker", "personal-finance",
            "cash-flow", "invoice", "invoicing", "billing",
            "payment", "payments", "payment-processing", "payment-gateway",
            "payment-integration", "transactions", "transaction",
            "stripe", "stripe-payments", "paypal", "square", "braintree",
            "credit-card", "card", "debit-card",
            "banking", "fintech-app", "neobank", "open-banking",
            "business-intelligence", "business-analytics", "economics",
            "bi", "powerbi", "powerbi-report", "tableau",
            "exchange", "forex", "fx", "currency", "crypto-exchange",
            "arbitrage", "trader", "eth-arbitrage",
            "earning", "income", "profit", "cash", "money", "gain",
            "revenue", "sales", "cost", "expense",
            "binance", "binance-api", "coinbase", "kraken", "ftx",
            "yahoo-finance", "alpha-vantage", "financial-data",
            "market", "market-data", "market-analysis",
            "risk", "risk-management", "risk-analysis",
            "aml", "kyc", "compliance", "regulatory", "audit",
            "tax", "taxation", "accounting-software",
            "insurance", "insure-tech",
            "pos", "point-of-sale", "retail", "ecommerce-analytics",
            "subscription", "recurring-billing", "saas-billing",
            "legal", "legal-tech", "contract",
            "operations-research", "optimization", "profit-maximization",
            "supply-chain", "logistics", "inventory", "procurement",
            "erp", "crm", "salesforce", "hubspot", "odoo"
        )
    )

    /**
     * 제외할 일반적인 토픽 (너무 광범위하거나 의미 없는 것들)
     */
    val EXCLUDED_TOPICS = setOf(
        // 일반 단어
        "open", "free", "simple", "basic", "general", "common",
        "learn", "learning", "tutorial", "guide", "example", "examples",
        "demo", "test", "experimental", "poc", "prototype", "proof-of-concept",
        "project", "template", "boilerplate", "starter", "starter-template", "starter-kit",
        "awesome", "collection", "list", "resources", "awesome-list", "awesome-lists",
        // 연도
        "2025", "2024", "2023", "2026", "hacktoberfest", "hacktoberfest2025",
        "hacktoberfest2024", "hacktoberfest2023", "hacktoberfest2022",
        "hacktoberfest2021", "hacktoberfest2020", "hacktoberfest-accepted",
        // 커뮤니티
        "opensource", "open-source", "community", "repository", "repositories",
        "good-first-issue", "course", "resume", "assistant",
        "peer-to-peer", "p2p", "client-side", "high-performance",
        "compliance", "healthcare", "games", "unix", "seed",
        "build", "vibe-coding", "vibecode",
        // 학습/교육
        "learning-resources", "educational-resources", "educational-project",
        "educational-tools", "students", "study", "curriculum",
        "internship", "internships", "course-project", "university-project",
        "assignment", "homework", "exam", "assessment",
        "udemy", "coursera", "edx", "mooc",
        "learning", "learn", "tutorial", "tutorials", "guide", "guides",
        "interactive-learning", "e-learning",
        // 기타 일반
        "opensource", "foss", "open-access", "open-data", "open-science",
        "contributions", "contributions-welcome", "contribution",
        "help-wanted", "good-first-issue", "beginner-friendly", "for-beginners",
        "personal", "personal-project", "pet-project", "hobby-project",
        "portfolio", "portfolio-page", "portfolio-site", "portfolio-website",
        "personal-portfolio", "porfolio",
        "homepage", "website", "webpage",
        "skills", "samples", "code-samples", "code-examples",
        "reference", "specifications", "specification", "standard", "standards",
        "addon", "component", "plugin", "extension", "widget", "widgets",
        // 너무 광범위한 토픽
        "software", "software-tools", "software-library", "software-architecture",
        "software-testing", "software-library",
        "application", "applications", "development", "app",
        "technology", "tech", "digital", "online", "internet",
        "framework", "frameworks", "library", "libraries",
        "product", "platform", "service", "services",
        "tools", "tool", "utility", "utilities", "util", "utils",
        "manager", "management", "admin", "administration",
        "system", "systems", "system-tools",
        "data", "database", "databases", "db", "dbms",
        "api", "backend", "frontend", "fullstack",
        "web", "mobile", "desktop",
        "cloud", "serverless",
        "automation", "automatic",
        "integration", "enterprise-integration",
        // 문서/README
        "readme", "readme-md", "documentation", "docs",
        "awesome-readme", "technical-documentation",
        "user-guide", "manual", "handbook",
        // 프로젝트 타입
        "mvp", "poc", "proof-of-concept", "prototype", "prototyping",
        "demo", "demo-project", "demo-app",
        "example", "examples", "sample", "samples",
        "template", "templates", "boilerplate", "boilerplate-template",
        "starter", "starter-template", "starter-kit", "quickstart",
        "scaffold", "scaffolding", "skeleton",
        // 상태/품질
        "beta", "alpha", "experimental", "wip", "work-in-progress",
        "deprecated", "legacy", "archived", "archive",
        "stable", "production", "production-ready",
        "research", "research-project", "research-tool", "research-tools",
        // 라이선스/법적
        "mit-license", "apache", "gpl", "bsd", "license",
        "legal", "terms-of-service", "terms-and-conditions", "privacy-policy",
        // 기타 커뮤니티
        "collaboration", "collaborate", "collaborative", "collaboration-tools",
        "work-from-home", "remote", "remote-development", "remote-work",
        "team", "teams", "teamwork", "pair-programming",
        "code-review", "pull-request", "pull-requests", "pr",
        "issue", "issues", "bug", "bugs", "bugfix",
        "commit", "commits", "contribution-graph",
        // 범용 형용사
        "awesome", "amazing", "cool", "best", "top", "popular", "trending",
        "new", "latest", "modern", "advanced", "professional",
        "easy", "simple", "quick", "fast", "lightweight",
        "powerful", "complete", "full", "comprehensive",
        "free", "open", "public", "shared",
        "custom", "customizable", "configurable", "flexible",
        "scalable", "scalable-architecture", "extensible",
        "multi", "multi-platform", "cross-platform", "multiplatform",
        "universal", "generic", "common",
        // 동작/상태
        "install", "installer", "setup", "update", "upgrade",
        "deploy", "deployment", "build", "building",
        "test", "testing", "debug", "debugging",
        "monitor", "monitoring", "track", "tracking",
        // 기타
        "fun", "game", "games", "gaming",
        "misc", "miscellaneous", "other", "others", "various",
        "collection", "collections", "list", "lists", "starred",
        "notes", "note", "blog", "blogs", "article", "articles",
        "book", "books", "ebook", "ebooks", "writing",
        "video", "videos", "tutorial-video",
        "podcast", "podcasts",
        "music", "song", "sound", "audio",
        "image", "images", "photo", "photos", "picture",
        "text", "document", "documents", "file", "files",
        // 지역/언어
        "english", "english-language", "chinese", "japanese", "korean",
        "french", "german", "spanish", "portuguese", "russian",
        "vietnamese", "vietnamese-language", "arabic", "hindi",
        "language", "languages", "natural-language",
        // 산업/도메인 (너무 일반적)
        "business", "enterprise", "commerce", "retail",
        "healthcare", "medical", "health",
        "education", "educational",
        "finance", "financial",
        "science", "scientific", "science-research",
        "engineering", "it", "information-technology",
        // 특정 회사/제품 (너무 일반적)
        "github", "google", "microsoft", "amazon", "aws", "azure",
        "facebook", "twitter", "instagram", "youtube", "tiktok",
        // 기타 제외
        "hactoberfest", "advent-of-code", "advent-of-code-2025",
        "ctf", "capture-the-flag",
        "conference", "conferences", "meetup", "meetups",
        "community-project", "open-terms-archive",
        "seanpm2001-", "seanpm2001-web", "seanpm2001-vexillology",
        "seanpm2001-games", "seanpm2001-lifearchive", "seanpm2001-flag-simulator",
        "flag-simulator", "flag-simulator-development",
        "softmicro-", "softmicro-documentation", "softmicro-drapes", "softmicro-project",
        "openad-", "openad-specification", "openad-development",
        "snu-2d", "snu-programming-tools"
    )
}