<template>
<div class="appointment-page">
<div class="top-bar">
<button class="back-btn" @click="goBack">‹</button>
<div class="top-title">预约服务</div>
<div class="top-spacer"></div>
</div>

<div class="form-shell">
<div class="merchant-card">
<div class="merchant-name">{{ merchant.name }}</div>
<div class="merchant-address">{{ merchant.address }}</div>
</div>

<div class="section-card">
<div class="section-title">选择服务 <span class="required">*</span></div>
<div class="option-list">
<button
v-for="item in serviceOptions"
:key="item.name"
:class="['option-row', { active: selectedService === item.name }]"
@click="selectedService = item.name"
>
<span>{{ item.name }}</span>
<span class="price">{{ item.price }}</span>
</button>
</div>
</div>

<div class="section-card">
<div class="section-title">选择日期 <span class="required">*</span></div>
<div class="chip-grid">
<button
v-for="item in dateOptions"
:key="item.key"
:class="['chip', { active: selectedDateKey === item.key }]"
@click="selectedDateKey = item.key"
>
{{ item.label }}
</button>
</div>
</div>

<div class="section-card">
<div class="section-title">选择时间 <span class="required">*</span></div>
<div class="chip-grid">
<button
v-for="slot in availableTimeOptions"
:key="slot.value"
:class="['chip', { active: selectedTime === slot.value, disabled: slot.disabled }]"
:disabled="slot.disabled"
@click="selectTime(slot)"
>
{{ slot.value }}
</button>
</div>
</div>

<div class="section-card note-card">
<div class="section-title">备注</div>
<textarea
v-model.trim="remark"
class="remark-input"
placeholder="请输入特殊要求或备注信息..."
></textarea>
</div>

<div class="action-bar">
<button class="action-btn cancel-btn" @click="cancelBooking">取消</button>
<button class="action-btn confirm-btn" @click="confirmBooking">确认预约</button>
</div>
</div>
</div>
</template>

<script>
export default {
name: "AppointmentView",

data() {
return {
selectedService: "",
selectedDateKey: "",
selectedTime: "",
remark: "",
fallbackMerchant: {
name: "爱宠美容工作室",
address: "朝阳区建国路88号",
price: "¥88起",
projects: [
{ name: "基础洗澡", price: "¥88" },
{ name: "精致美容", price: "¥158" },
{ name: "豪华SPA", price: "¥288" }
]
},
dateOptions: [],
timeOptions: ["09:00", "10:00", "11:00", "14:00", "15:00", "16:00", "17:00", "18:00"]
}
},

computed: {
merchant() {
const query = this.$route.query || {}

return {
...this.fallbackMerchant,
name: query.name || this.fallbackMerchant.name,
address: query.address || this.fallbackMerchant.address,
price: query.price || this.fallbackMerchant.price,
projects: this.normalizeProjects(query.projects, this.fallbackMerchant.projects)
}
},

serviceOptions() {
if (this.merchant.projects && this.merchant.projects.length) {
return this.merchant.projects
}

return [
{ name: "基础洗澡", price: this.merchant.price },
{ name: "精致美容", price: "¥158" },
{ name: "豪华SPA", price: "¥288" }
]
},

availableTimeOptions() {
const isToday = this.selectedDateKey === this.dateOptions[0]?.key
const now = new Date()
const currentMinutes = now.getHours() * 60 + now.getMinutes()

return this.timeOptions.map(value => {
if (!isToday) {
return { value, disabled: false }
}

const [h, m] = value.split(":").map(Number)
const slotMinutes = h * 60 + m

return {
value,
disabled: slotMinutes <= currentMinutes
}
})
}
},

mounted() {
this.initDateOptions()

if (this.serviceOptions.length) {
this.selectedService = this.serviceOptions[0].name
}

this.selectedDateKey = this.dateOptions[0]?.key || ""
},

watch: {
availableTimeOptions() {
const active = this.availableTimeOptions.find(item => item.value === this.selectedTime)

if (active && active.disabled) {
this.selectedTime = ""
}
}
},

methods: {
initDateOptions() {
const list = []
const now = new Date()

for (let i = 0; i < 7; i++) {
const d = new Date(now)
d.setDate(now.getDate() + i)

const month = d.getMonth() + 1
const day = d.getDate()
const key = `${d.getFullYear()}-${month}-${day}`

let label = `${month}/${day}`
if (i === 0) {
label = "今天"
} else if (i === 1) {
label = "明天"
}

list.push({ key, label })
}

this.dateOptions = list
},

selectTime(slot) {
if (slot.disabled) {
return
}

this.selectedTime = slot.value
},

normalizeProjects(value, fallback) {
if (!value) {
return fallback
}

if (Array.isArray(value)) {
return value
}

if (typeof value === "string") {
try {
const parsed = JSON.parse(value)
if (Array.isArray(parsed)) {
return parsed
}
} catch (error) {
return fallback
}
}

return fallback
},

goBack() {
this.$router.back()
},

cancelBooking() {
this.goBack()
},

confirmBooking() {
if (!this.selectedService || !this.selectedDateKey || !this.selectedTime) {
window.alert("请先选择服务、日期和时间")
return
}

window.alert("预约成功，商户会尽快与您确认")
}
}
}
</script>

<style scoped>
.appointment-page {
min-height: 100vh;
background: #f3f3f6;
}

.top-bar {
height: 72px;
display: flex;
align-items: center;
justify-content: space-between;
background: #fff;
padding: 0 14px;
border-bottom: 1px solid #e8e8ee;
}

.back-btn {
border: none;
background: transparent;
width: 34px;
height: 34px;
font-size: 30px;
line-height: 34px;
color: #222;
text-align: left;
cursor: pointer;
}

.top-title {
font-size: 22px;
font-weight: 700;
color: #2f2a2a;
}

.top-spacer {
width: 34px;
}

.form-shell {
max-width: 760px;
margin: 0 auto;
padding: 0 0 24px;
}

.merchant-card,
.section-card {
background: #fff;
padding: 18px 20px;
margin-top: 10px;
}

.merchant-name {
font-size: 18px;
line-height: 1.2;
font-weight: 700;
color: #1f1a1a;
}

.merchant-address {
margin-top: 8px;
font-size: 14px;
color: #6f6a80;
}

.section-title {
font-size: 18px;
font-weight: 500;
color: #3a344b;
margin-bottom: 14px;
}

.required {
color: #ff3b3b;
}

.option-list {
display: grid;
gap: 10px;
}

.option-row {
height: 54px;
border: 1px solid #d9dce3;
border-radius: 14px;
background: #fff;
display: flex;
justify-content: space-between;
align-items: center;
padding: 0 14px;
font-size: 16px;
color: #2b263a;
cursor: pointer;
}

.option-row .price {
color: #8f84d2;
}

.option-row.active {
border-color: #8673d6;
box-shadow: 0 0 0 2px rgba(134, 115, 214, 0.12) inset;
}

.chip-grid {
display: grid;
grid-template-columns: repeat(4, minmax(0, 1fr));
gap: 10px;
}

.chip {
height: 48px;
border: 1px solid #d9dce3;
border-radius: 14px;
background: #fff;
font-size: 16px;
color: #3f3852;
cursor: pointer;
}

.chip.active {
border-color: #8673d6;
background: #f2eefb;
color: #5c4da8;
}

.chip:disabled,
.chip.disabled {
cursor: not-allowed;
color: #b9b4c6;
background: #f7f7f9;
border-color: #e8e8ee;
}

.note-card {
padding-bottom: 20px;
}

.remark-input {
width: 100%;
min-height: 120px;
border: 1px solid #d9dce3;
border-radius: 14px;
padding: 12px 14px;
font-size: 16px;
color: #2f2a38;
resize: vertical;
outline: none;
}

.remark-input:focus {
border-color: #8673d6;
}

.remark-input::placeholder {
color: #aaa5b5;
}

.action-bar {
position: sticky;
bottom: 0;
z-index: 8;
display: grid;
grid-template-columns: 1fr 1fr;
gap: 10px;
padding: 12px 0 6px;
background: linear-gradient(to top, #f3f3f6 70%, rgba(243, 243, 246, 0));
}

.action-btn {
height: 46px;
border: none;
border-radius: 12px;
font-size: 16px;
cursor: pointer;
}

.cancel-btn {
background: #e8e8ee;
color: #4f4a61;
}

.confirm-btn {
background: #8673d6;
color: #fff;
}

@media (max-width: 640px) {
.merchant-name {
font-size: 17px;
}

.merchant-address {
font-size: 13px;
}

.section-title {
font-size: 16px;
}

.option-row,
.chip,
.remark-input {
font-size: 15px;
}

.chip-grid {
grid-template-columns: repeat(3, minmax(0, 1fr));
}
}
</style>
