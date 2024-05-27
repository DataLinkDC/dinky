{{/*
Expand the name of the chart.
*/}}
{{- define "dinky.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
*/}}
{{- define "dinky.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "dinky.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default common labels.
*/}}
{{- define "dinky.labels" -}}
helm.sh/chart: {{ include "dinky.chart" . }}
{{ include "dinky.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "dinky.selectorLabels" -}}
app.kubernetes.io/name: {{ include "dinky.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
 Create service account name
*/}}
{{- define "dinky.serviceAccountName" -}}
{{- if .Values.dinkyServiceAccount.create }}
{{- default (include "dinky.fullname" .) .Values.dinkyServiceAccount.name }}
{{- else }}
{{- default "default" .Values.dinkyServiceAccount.name }}
{{- end }}
{{- end }}

{{- define "dinky.dbActive" -}}
- name: DB_ACTIVE
  {{- if .Values.postgresql.enabled }}
  value: "pgsql"
  {{- else if .Values.mysql.enabled }}
  value: "mysql"
  {{- else }}
  value: {{ .Values.externalDatabase.type | quote }}
  {{- end }}
{{- end }}